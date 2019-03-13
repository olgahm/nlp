package es.upm.oeg.librairy.nlp.annotators;

import edu.stanford.nlp.pipeline.Annotation;
import es.upm.oeg.librairy.nlp.annotators.stanford.StanfordAnnotatorES;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class StanfordTest {

    private static final Logger LOG = LoggerFactory.getLogger(StanfordTest.class);


    @Test
    public void execute(){
        StanfordAnnotatorES pipeAnnotatorESOriginal = new StanfordAnnotatorES();

        String text = "Reglamento (CE) no 1087/2005 de la Comisión\\nde 8 de julio de 2005\\npor el que se modifica el Reglamento (CE) no 1210/2003 del Consejo relativo a determinadas restricciones específicas aplicables a las relaciones económicas y financieras con Iraq\\nLA COMISIÓN DE LAS COMUNIDADES EUROPEAS,\\nVisto el Tratado constitutivo de la Comunidad Europea,\\nVisto el Reglamento (CE) no 1210/2003 del Consejo, de 7 de julio de 2003, relativo a determinadas restricciones específicas aplicables a las relaciones económicas y financieras con Iraq y por el que se deroga el Reglamento (CE) no 2465/96 del Consejo [1] y, en particular, su artículo 11, letra b),\\nConsiderando lo siguiente:\\n(1) El anexo IV del Reglamento (CE) no 1210/2003 contiene una lista de personas físicas y jurídicas, entidades y organismos asociados con el régimen del antiguo Presidente Sadam Hussein a los que afecta el bloqueo de fondos y recursos económicos establecido en ese Reglamento.\\n(2) El 22 de junio de 2005, el Comité de Sanciones del Consejo de Seguridad de la ONU decidió modificar la citada lista, en la que figuran Sadam Hussein y otros oficiales de alto rango del régimen iraquí, sus parientes más inmediatos y las entidades poseídas o controladas por estas personas o por otras que actúan en su nombre o bajo su dirección, a quienes se aplica el bloqueo de fondos y recursos económicos. Procede, por tanto, modificar el Reglamento (CE) no 1210/2003 en consecuencia.\\n(3) A fin de velar por la eficacia de las medidas previstas en el presente Reglamento, éste debe entrar en vigor el día de su publicación.\\nHA ADOPTADO EL PRESENTE REGLAMENTO:\\nArtículo 1\\nEl anexo IV del Reglamento (CE) no 1210/2003 se modifica conforme a lo especificado en el anexo del presente Reglamento.\\nArtículo 2\\nEl presente Reglamento entrará en vigor el día de su publicación en el Diario Oficial de la Unión Europea.\\nEl presente Reglamento será obligatorio en todos sus elementos y directamente aplicable en cada Estado miembro.\\nHecho en Bruselas, el 8 de julio de 2005.\\nPor la Comisión\\nEneko Landáburu\\nDirector General de Relaciones Externas\\n[1] DO L 169 de 8.7.2003, p. 6. Reglamento modificado en último lugar por el Reglamento (CE) no 1566/2004 de la Comisión (DO L 285 de 4.9.2004, p. 6).\\n--------------------------------------------------\\nANEXO\\nEl anexo IV del Reglamento (CE) no 1210/2003 se modifica como sigue:\\nSe añadirán las siguientes personas físicas:\\n\\\"Muhammad Yunis Ahmad [alias a) Muhammad Yunis Al-Ahmed, b) Muhammad Yunis Ahmed, c) Muhammad Yunis Ahmad Al-Badrani, d) Muhammad Yunis Ahmed Al-Moali]. Direcciones: a) Al-Dawar Street, Bludan, Siria, b) Damasco, Siria, c) Mosul, Iraq, d) Wadi Al-Hawi, Iraq, e) Dubai, Emiratos Árabes Unidos, f) Al-Hasaka, Siria. Fecha de nacimiento: 1949. Lugar de nacimiento: Al-Mowall, Mosul, Iraq. Nacionalidad: iraquí.\\\"\\n--------------------------------------------------\\n";

        Annotation result = pipeAnnotatorESOriginal.annotate(text);
        LOG.info("Result: " + result);

    }
}
