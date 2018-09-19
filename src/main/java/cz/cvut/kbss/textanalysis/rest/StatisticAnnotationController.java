package cz.cvut.kbss.textanalysis.rest;

import cz.cvut.kbss.textanalysis.model.StatisticAnnotationsResult;
import cz.cvut.kbss.textanalysis.service.StatisticalAnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StatisticAnnotationController {

    @Autowired
    private StatisticalAnnotationService annotationService;

    @RequestMapping("/annotate-statistic")
    public List<StatisticAnnotationsResult> getAnnotationResult() throws Exception {
        return annotationService.getAnnotationServiceOutput();
    }


}
