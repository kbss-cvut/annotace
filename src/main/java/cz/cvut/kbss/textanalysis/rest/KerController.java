package cz.cvut.kbss.textanalysis.rest;

import cz.cvut.kbss.textanalysis.model.KerResult;
import cz.cvut.kbss.textanalysis.service.KerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class KerController {

    @Autowired
    private KerService kerService;

    @RequestMapping("/ker-result")
    public ResponseEntity<KerResult> getKerResult() throws IOException {
        return kerService.getKerResult();
    }

}
