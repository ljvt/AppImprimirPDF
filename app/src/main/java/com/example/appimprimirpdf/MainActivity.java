package com.example.appimprimirpdf;

import android.Manifest;
import android.content.Context;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button btn_create_pdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_create_pdf = findViewById(R.id.btn_create_pdf);

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        btn_create_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createPDFFile(Common.getAppPath(MainActivity.this) + "test_pdf.pdf");
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();

    }

    private void createPDFFile(String path) {
        if (new File(path).exists()) {
            new File(path).delete();
        }
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            document.addCreationDate();
            document.addAuthor("");
            document.addCreator("");

            //font setting
            BaseColor colorAccent = new BaseColor(0, 153, 204, 255);
            float fontSize = 20.0f;
            float valueFontSize = 26.0f;

            //custom font
            BaseFont fontName = BaseFont.createFont("assets/fonts/BrandonText-Medium.otf", "UTF-8", BaseFont.EMBEDDED);

            //create title of document
            Font titleFont = new Font(fontName, 36.0f, Font.NORMAL, BaseColor.BLACK);
            addNewItem(document, "Order Detail", Element.ALIGN_CENTER, titleFont);

            //add more
            Font orderNumderFont = new Font(fontName, fontSize, Font.NORMAL, colorAccent);
            addNewItem(document, "Order No", Element.ALIGN_LEFT, orderNumderFont);

            Font orderNumderValueFont = new Font(fontName, valueFontSize, Font.NORMAL, BaseColor.BLACK);
            addNewItem(document, "#717171", Element.ALIGN_LEFT, orderNumderValueFont);

            addLineSeparator(document);

            addNewItem(document, "Order Date", Element.ALIGN_LEFT, orderNumderFont);
            addNewItem(document, "23/08/2020", Element.ALIGN_LEFT, orderNumderValueFont);

            addLineSeparator(document);

            addNewItem(document, "Account Name", Element.ALIGN_LEFT, orderNumderFont);
            addNewItem(document, "Eddy Lee", Element.ALIGN_LEFT, orderNumderValueFont);

            addLineSeparator(document);

            //add product order detail
            addLineSeparator(document);
            addNewItem(document,"Product Detail", Element.ALIGN_CENTER, titleFont);
            addLineSeparator(document);

            //item 1
            addNewItemWithLeftAndRight(document,"Pizza 25","(0.0%)", titleFont, orderNumderFont);
            addNewItemWithLeftAndRight(document,"12*100","12000.00", titleFont, orderNumderFont);

            addLineSeparator(document);

            //item 2
            addNewItemWithLeftAndRight(document,"Pizza 26","(0.0%)", titleFont, orderNumderFont);
            addNewItemWithLeftAndRight(document,"10*100","10000.00", titleFont, orderNumderFont);

            addLineSeparator(document);

            //total
            addLinespace(document);
            addLinespace(document);

            addNewItemWithLeftAndRight(document,"Total","22000.00", titleFont, orderNumderFont);

            Toast.makeText(this, "Sucess", Toast.LENGTH_SHORT).show();
            printPDF();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

    private void printPDF() {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        try {
            PrintDocumentAdapter printDocumentAdapter = new PdfDoumentAdapter(MainActivity.this, Common.getAppPath(MainActivity.this) + "test_pdf.pdf");
            printManager.print("Document", printDocumentAdapter, new PrintAttributes.Builder().build());
        } catch (Exception e) {
            Log.i("Erro print pdf"," = " + e.getMessage());
        }
    }

    private void addNewItemWithLeftAndRight(Document document, String textLeft, String textRight, Font textFontLeft, Font textFontRight) {
        try {
            Chunk chunkTextLeft = new Chunk(textLeft, textFontLeft);
            Chunk chunkTextRight = new Chunk(textRight, textFontRight);
            Paragraph p = new Paragraph(chunkTextLeft);
            p.add(new Chunk(new VerticalPositionMark()));
            p.add(chunkTextRight);
            document.add(p);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void addLineSeparator(Document document) {
        try {
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));
            addLinespace(document);
            document.add(new Chunk(lineSeparator));
            addLinespace(document);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void addLinespace(Document document) {
        try {
            document.add(new Paragraph(""));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void addNewItem(Document document, String text, int align, Font font) {
        try {
            Chunk chunk = new Chunk(text, font);
            Paragraph paragraph = new Paragraph(chunk);
            paragraph.setAlignment(align);
            document.add(paragraph);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}