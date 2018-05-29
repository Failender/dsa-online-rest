package de.failender.dsaonline.jobs;

import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.service.UserHeldenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RefreshHeldenJob {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserHeldenService userHeldenService;

	@Value("${dsa.online.helden.refresh.break:1000}")
	private int breakTime;

	@Value("${dsa.online.helden.refresh.run.once}")
	private boolean runOnce;

	private int runCount;


	@Scheduled(fixedRateString = "${dsa.online.helden.refresh.interval}", initialDelayString = "${dsa.online.helden.refresh.interval}")
	public void run() {

		if (runOnce && runCount == 1) {
			return;
		}
		runCount++;
		log.info("RefreshHeldenJob starting");
		for (UserEntity userEntity : userRepository.findAll()) {
			if (userEntity.getToken() != null) {
				userHeldenService.forceUpdateHeldenForUser(userEntity);
				try {
					Thread.sleep(breakTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
