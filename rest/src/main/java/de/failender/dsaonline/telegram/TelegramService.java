package de.failender.dsaonline.telegram;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.entity.VersionEntity;
import de.failender.dsaonline.hooks.HeldenHooks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
@ConditionalOnProperty("dsa.online.telegram.enabled")
public class TelegramService implements HeldenHooks {


	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TelegramService.class);
	private final ObjectMapper objectMapper;


	private final String externalUri;
	private TelegramConfig telegramConfig;
	private final TelegramBot telegramBot;

	public TelegramService(@Value("${dsa.online.telegram:}") String apiKey, ObjectMapper objectMapper, @Value("${dsa.online.external.adress}") String externalUri) {

		this.objectMapper = objectMapper;
		this.externalUri = externalUri;
		telegramBot = new TelegramBot(apiKey);
		tryLoadConfig();

		telegramBot.setUpdatesListener(updates -> {

			for (Update update : updates) {
				System.out.println(update);
				if(update.message().chat().type() == Chat.Type.group || update.message().chat().type() == Chat.Type.Private) {
					if(update.message().text().equals("/start")) {
						long chatId = update.message().chat().id();
						SendResponse response = telegramBot.execute(new SendMessage(chatId, "Hello!"));
						telegramConfig.getListeningChats().add(update.message().chat().id());
						saveConfig();

					}
				}
			}
			return UpdatesListener.CONFIRMED_UPDATES_ALL;
		});
	}

	private void messageListener(String message) {
		log.info("Sending message {}", message);
		for (Long listeningChat : telegramConfig.getListeningChats()) {
			telegramBot.execute(new SendMessage(listeningChat, message));
		}
	}

	private static final String FILE_NAME = "configuration/telegram.json";

	private void tryLoadConfig() {
		File file = new File(FILE_NAME);
		if(file.exists()) {
			try {
				telegramConfig = objectMapper.readValue(file, TelegramConfig.class);
				log.info("Found config file. Listening chats are: " + telegramConfig.getListeningChats());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			telegramConfig = new TelegramConfig();
		}
	}

	private void saveConfig() {
		try {
			File file = new File(FILE_NAME);
			if(!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			objectMapper.writeValue(new File(FILE_NAME), this.telegramConfig);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void versionCreated(UserEntity userEntity, HeldEntity heldEntity, VersionEntity versionEntity) {
		messageListener("Eine Neue Version für den Helden " + heldEntity.getName() + " wurde angelegt! Neue Version: " + versionEntity.getVersion() + " " + compare(heldEntity, versionEntity));
	}

	@Override
	public void heldCreated(UserEntity userEntity, VersionEntity versionEntity, HeldEntity heldEntity) {
		messageListener("Ein neuer Held  " + heldEntity.getName() + " wurde angelegt! Besitzer: " + userEntity.getName() + " " + overview(heldEntity, versionEntity));
	}

	private String overview(HeldEntity heldEntity, VersionEntity versionEntity) {
		return this.externalUri + "/held/uebersicht?held=" + heldEntity.getId() + "&version=" + versionEntity.getVersion();
	}

	private String compare(HeldEntity heldEntity, VersionEntity versionEntity) {
		return this.externalUri + "/held/vergleichen/" + heldEntity.getId() + "/" + (versionEntity.getVersion() -1) + "/" + versionEntity.getVersion();
	}




	public static class TelegramConfig {
		private Set<Long> listeningChats = new HashSet<>();


		public Set<Long> getListeningChats() {
			return listeningChats;
		}
	}
}
