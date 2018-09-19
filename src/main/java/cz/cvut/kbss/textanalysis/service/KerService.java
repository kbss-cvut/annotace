package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.textanalysis.model.KerResult;
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
    private KerResult kerResult;

    private File file = new File("C:/Projects/OPPPR/services/textanalysis/src/main/resources/test.txt");

    public KerService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public KerResult getKerResult() {

        String kerUrl = "http://lindat.mff.cuni.cz/services/ker?language=cs&threshold=0.5&maximum-words=30";
        Resource fileResource = new FileSystemResource(file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        KerResult response = this.restTemplate.postForObject(kerUrl, requestEntity, KerResult.class);

        return response;

    }

    public static Resource getTestFile() throws IOException {
        Path testFile = Files.createTempFile("test-file", ".txt");
        Files.write(testFile, "This is a test file.".getBytes());
        return new FileSystemResource(testFile.toFile());
    }
}
