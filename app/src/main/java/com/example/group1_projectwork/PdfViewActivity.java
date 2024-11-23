package com.example.group1_projectwork;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

public class PdfViewActivity extends AppCompatActivity {

    private ImageView pdfImageView;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private int totalPages;
    private int currentPageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);

        pdfImageView = findViewById(R.id.pdfImageView);
        Button backButton = findViewById(R.id.backButton);
        Button nextButton = findViewById(R.id.nextButton);
        Button prevButton = findViewById(R.id.prevButton);

        // Get the PDF path passed from the previous activity
        String pdfPath = getIntent().getStringExtra("pdfPath");
        if (pdfPath != null) {
            openPdf(pdfPath);
        } else {
            Toast.makeText(this, "Invalid PDF path", Toast.LENGTH_SHORT).show();
        }

        // Set up back button
        backButton.setOnClickListener(v -> onBackPressed());

        // Set up next and previous page buttons
        nextButton.setOnClickListener(v -> {
            if (currentPageIndex < totalPages - 1) {
                renderPage(++currentPageIndex);
            }
        });

        prevButton.setOnClickListener(v -> {
            if (currentPageIndex > 0) {
                renderPage(--currentPageIndex);
            }
        });
    }

    private void openPdf(String pdfPath) {
        try {
            File file = new File(pdfPath);
            ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(parcelFileDescriptor);
            totalPages = pdfRenderer.getPageCount();

            // Render the first page
            renderPage(0);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error opening PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void renderPage(int pageIndex) {
        // Close the previous page if it's open
        if (currentPage != null) {
            currentPage.close();
        }

        // Open the desired page
        currentPage = pdfRenderer.openPage(pageIndex);

        // Create a bitmap and render the page into it
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        // Set the rendered bitmap to the ImageView
        pdfImageView.setImageBitmap(bitmap);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (currentPage != null) {
            currentPage.close();
        }
        if (pdfRenderer != null) {
            pdfRenderer.close();
        }
    }
}
