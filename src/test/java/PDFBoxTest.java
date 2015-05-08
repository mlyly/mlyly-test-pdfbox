import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.*;
import org.junit.Test;

import java.io.IOException;
import java.util.logging.Logger;

public class PDFBoxTest {

    private static final Logger LOG = Logger.getAnonymousLogger();

    @Test
    public void testPdfBox() throws IOException, COSVisitorException {
        LOG.info("testPdfBox()");

        String docFileName = "test.pdf";

        LOG.info("load: " + docFileName);
        PDDocument doc = PDDocument.load(this.getClass().getResourceAsStream(docFileName));
        LOG.info("loaded: " + docFileName);

        PDDocumentCatalog pdDocumentCatalog = doc.getDocumentCatalog();
        PDAcroForm pdAcroForm = pdDocumentCatalog.getAcroForm();

        for (Object fieldObject : pdAcroForm.getFields()) {
            PDField pdField = (PDField) fieldObject;

            LOG.info("** field: " + pdField.getFullyQualifiedName() + " / " + pdField.getPartialName() + " - " + pdField.getFieldType() + ", class=" + pdField.getClass());

            if (pdField instanceof PDRadioCollection) {
                PDRadioCollection pdRadioCollection = (PDRadioCollection) pdField;

                for (Object o : pdRadioCollection.getKids()) {
                    PDField kid = (PDField) o;
                    LOG.info("  radio kid: " + kid.getFullyQualifiedName() + " / " + kid.getPartialName() + ", " + kid.getClass() + ", value=" + kid.getValue());

                    if (kid instanceof PDCheckbox) {
                        LOG.info("    ON: " + ((PDCheckbox) kid).getOnValue());
                        LOG.info("    OFF: " + ((PDCheckbox) kid).getOffValue());
                        pdRadioCollection.setValue(((PDCheckbox) kid).getOnValue());
                    }
                    // pdRadioCollection.setValue(kid.getPartialName());
                }
            } else if (pdField instanceof PDPushButton) {
                PDPushButton pdPushButton = (PDPushButton) pdField;
                if (pdPushButton.getKids() != null) {
                    for (Object o : pdPushButton.getKids()) {
                        PDField kid = (PDField) o;
                        LOG.info("  push kid: " + kid.getFullyQualifiedName() + " / " + kid.getPartialName() + ", " + kid.getClass() + ", value=" + kid.getValue());
                        pdPushButton.setValue(kid.getPartialName());
                    }
                }
            } else if (pdField instanceof PDCheckbox) {
                // ((PDCheckbox) pdField).check();
                if (Math.random() < 0.5) {
                    pdField.setValue(((PDCheckbox) pdField).getOnValue());
                }
            } else if (pdField instanceof PDTextbox) {
                ((PDTextbox) pdField).setValue("[" + pdField.getFullyQualifiedName() + "]");
            } else {
                LOG.info("UNHANDLED TYPE: " + pdField.getFieldType() + ", " + pdField.getClass());
            }
        }

        doc.save("/tmp/output_test.pdf");

        doc.close();
        LOG.info("done.");

    }

    private void print(String fmt, Object... args) {
        System.out.printf(fmt, args);
    }

}
