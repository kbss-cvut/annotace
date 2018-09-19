package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.textanalysis.model.MorphoDitaResultJson;
import java.util.List;

public interface MorphoDitaServiceAPI {

    List<List<MorphoDitaResultJson>> getMorphoDiteResultProcessed(String s);
}
