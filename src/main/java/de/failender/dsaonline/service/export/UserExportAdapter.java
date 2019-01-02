package de.failender.dsaonline.service.export;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.rest.dto.UserData;
import de.failender.dsaonline.service.UserService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Component
public class UserExportAdapter implements ExportAdapter {

    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final UserRepository userRepository;

    public UserExportAdapter(ObjectMapper objectMapper, UserService userService, UserRepository userRepository) {
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public void export(ZipOutputStream zos) throws IOException {
        zos.putNextEntry(new ZipEntry("user.json"));
        zos.write(objectMapper.writeValueAsBytes(userRepository.findAll().stream().map(user -> {
            List<String> roles = userRepository.findRoleNamesForUser(user.getId());
            List<String> meister = userRepository.getMeisterGruppenNames(user.getId());
            return new UserData(user.getName(), user.getToken(), roles, meister, user.getGruppe().getName(), user.getPassword());
        }).collect(Collectors.toList())));
        zos.closeEntry();
    }

    @Override
    public void doImport(Map<Integer, Integer> groupMapping, ZipFile zipFile) throws IOException {
        ZipEntry entry = zipFile.getEntry("user.json");
        InputStream is = zipFile.getInputStream(entry);
        List<UserData> userData = objectMapper.readValue(is, new TypeReference<List<UserData>>() {
        });
        userService.createUsers(userData);
    }
}
