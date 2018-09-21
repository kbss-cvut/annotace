package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.textanalysis.model.KerResult;
import java.nio.file.Paths;
import java.util.Collections;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class KerService {

    private final RestTemplate restTemplate;

    public KerService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public KerResult getKerResult(final String chunks) {

        String kerUrl = "http://lindat.mff.cuni.cz/services/ker?language=cs&threshold=0.04&maximum-words=30";


        final File file;
        try {
            file = File.createTempFile("ker-input","");
            Files.write(Paths.get(file.toURI()), chunks.getBytes());

            Resource fileResource = new FileSystemResource(file);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            KerResult response = this.restTemplate.postForObject(kerUrl, requestEntity, KerResult.class);

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return KerResult.createEmpty();
        }
    }

    public static Resource getTestFile() throws IOException {
        Path testFile = Files.createTempFile("test-file", ".txt");
        Files.write(testFile, "This is a test file.".getBytes());
        return new FileSystemResource(testFile.toFile());
    }
}
