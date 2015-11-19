package com.mfodepositorsacc.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.codec.Base64;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.mfodepositorsacc.maincontract.HeaderSettings;
import com.mfodepositorsacc.settings.ProjectSettings;
import com.mfodepositorsacc.viewhelpers.AbstractITextPdfView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;

/**
 * Created by berz on 06.09.2015.
 */
public class ExamplePDF extends AbstractITextPdfView {


    @Override
    public boolean isDefaultMargins(){
        return false;
    }

    @Override
    public float getMarginTop(){
        return 90;
    }

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document doc,
                                    PdfWriter writer, HttpServletRequest request, HttpServletResponse response)
            throws Exception {


        // get data model which is passed by the Spring container
        HashMap<String, Object> data = (HashMap<String, Object>) model.get("data");
        HeaderSettings headerSettings = (HeaderSettings) data.get("headerSettings");
        HashMap<String, Object> placeholders = (HashMap<String, Object>) data.get("placeholders");

        String[] htmlBlocks = (String[]) data.get("htmlBlocks");

        writer.setPageEvent(new HeaderHelper(headerSettings));

        for(String html : htmlBlocks) {
            XMLWorkerHelper worker = XMLWorkerHelper.getInstance();

            for(String key : placeholders.keySet()){
                html = html.replace(key, placeholders.get(key).toString());
            }

            ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
            worker.parseXHtml(writer, doc, is, null, Charset.forName("UTF-8"));
        }

        // Распечатать
        //PdfAction action = new PdfAction(PdfAction.PRINTDIALOG);
        //writer.setOpenAction(action);

    }

    public class HeaderHelper extends PdfPageEventHelper{
        public HeaderHelper(HeaderSettings headerSettings){
            this.setHeaderSettings(headerSettings);
        }

        private HeaderSettings headerSettings;

        @Override
        public void onEndPage(PdfWriter writer, Document document){
            //ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Top Left"), 30, 800, 0);
            PdfPTable table = new PdfPTable(3);
            try {
                table.setWidths(this.getHeaderSettings().getTableColsWidths());
                table.setTotalWidth(this.getHeaderSettings().getTableWidth());
                table.setLockedWidth(true);
                table.getDefaultCell().setFixedHeight(20);
                table.getDefaultCell().setBorder(Rectangle.BOTTOM);
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

                ClassPathResource classPathResource = new ClassPathResource("images/yazaimy_logo.png");
                Image image = Image.getInstance(classPathResource.getFile().getPath());

                ClassPathResource fontClassPathResource = new ClassPathResource("fonts/calibri.ttf");

                PdfPCell cell1 = new PdfPCell();
                cell1.setBorder(table.getDefaultCell().getBorder());
                cell1.addElement(image);
                table.addCell(cell1);

                BaseFont bf = BaseFont.createFont(
                        fontClassPathResource.getFile().getPath(),
                        "Cp1251",
                        BaseFont.EMBEDDED
                );
                Font f1 = new Font(bf);
                f1.setColor(18, 112, 0);
                f1.setSize(12);
                f1.setStyle("font-weight: bold");

                Font f2 = new Font(f1);
                f2.setSize(8);

                Font f3 = new Font(bf);
                f3.setSize(8);

                Font f4 = new Font(f3);
                f4.setColor(BaseColor.BLUE);

                PdfPCell cell2 = new PdfPCell();
                cell2.setBorder(table.getDefaultCell().getBorder());
                Paragraph p1 = new Paragraph("OOO «АКТИВ-ЭКСПРЕСС»", f1);
                p1.setAlignment(Element.ALIGN_CENTER);
                cell2.addElement(p1);
                Paragraph p2 = new Paragraph("Название бренда «ЯЗАЙМУ»", f2);
                p2.setAlignment(Element.ALIGN_CENTER);
                cell2.addElement(p2);
                Paragraph p3 = new Paragraph("ОГРН 1106630000586,", f3);
                p3.setAlignment(Element.ALIGN_CENTER);
                cell2.addElement(p3);
                Paragraph p4 = new Paragraph("ИНН: 7722317017, / КПП: 772201001", f3);
                p4.setAlignment(Element.ALIGN_CENTER);
                cell2.addElement(p4);
                cell2.setHorizontalAlignment(table.getDefaultCell().getHorizontalAlignment());
                table.addCell(cell2);

                PdfPCell cell3 = new PdfPCell();
                cell3.addElement(new Paragraph("Телефон: +7 (499) 343-16-91", f3));
                Paragraph p5 = new Paragraph("Email: ", f3);
                Chunk email = new Chunk("info@finnal.ru");
                email.setFont(f4);
                email.setAction(new PdfAction(new URL("mailto:info@finnal.ru")));
                p5.add(email);
                cell3.addElement(p5);
                Paragraph p6 = new Paragraph("Сайт: ", f3);
                Chunk site = new Chunk("https://finnal.ru", f4);
                site.setAction(new PdfAction(new URL("https://finnal.ru")));
                p6.add(site);
                cell3.addElement(p6);
                cell3.addElement(new Paragraph("111024, г. Москва, ул. Авиамоторная, д. 50, ", f3));
                cell3.addElement(new Paragraph("стр. 2, пом IX - ком. 51", f3));

                cell3.setBorder(table.getDefaultCell().getBorder());
                cell3.setHorizontalAlignment(table.getDefaultCell().getHorizontalAlignment());
                table.addCell(cell3);
                table.writeSelectedRows(
                        this.getHeaderSettings().getRowStart(),
                        this.getHeaderSettings().getRowEnd(),
                        this.getHeaderSettings().getxPos(),
                        this.getHeaderSettings().getyPos(),
                        writer.getDirectContent()
                );
            }
            catch(DocumentException de) {
                throw new ExceptionConverter(de);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public HeaderSettings getHeaderSettings() {
            return headerSettings;
        }

        public void setHeaderSettings(HeaderSettings headerSettings) {
            this.headerSettings = headerSettings;
        }
    }

}
