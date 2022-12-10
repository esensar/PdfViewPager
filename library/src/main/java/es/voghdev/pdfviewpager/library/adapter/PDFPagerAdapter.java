/*
 * Copyright (C) 2016 Olmo Gallegos Hernández.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.voghdev.pdfviewpager.library.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.view.View;
import android.view.ViewGroup;

import es.voghdev.pdfviewpager.library.R;
import es.voghdev.pdfviewpager.library.subscaleview.ImageSource;
import es.voghdev.pdfviewpager.library.subscaleview.SubsamplingScaleImageView;
import es.voghdev.pdfviewpager.library.util.EmptyClickListener;

public class PDFPagerAdapter extends BasePDFPagerAdapter {

    View.OnClickListener pageClickListener = new EmptyClickListener();

    public PDFPagerAdapter(Context context, String pdfPath, PdfErrorHandler errorHandler) {
        super(context, pdfPath, errorHandler);
    }

    @Override
    @SuppressWarnings("NewApi")
    public Object instantiateItem(ViewGroup container, int position) {
        View v = inflater.inflate(R.layout.view_pdf_page, container, false);
        SubsamplingScaleImageView ssiv = v.findViewById(R.id.subsamplingImageView);

        if (renderer == null || getCount() < position) {
            return v;
        }

        PdfRenderer.Page page = getPDFPage(renderer, position);

        Bitmap bitmap = bitmapContainer.get(position);
        ssiv.setImage(ImageSource.bitmap(bitmap));

        ssiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageClickListener.onClick(v);
            }
        });

        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        page.close();

        container.addView(v, 0);

        return v;
    }

    @Override
    public void close() {
        super.close();
    }

    public static class Builder {
        Context context;
        String pdfPath = "";
        PdfErrorHandler errorHandler = new NullPdfErrorHandler();
        View.OnClickListener pageClickListener = new EmptyClickListener();

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setPdfPath(String path) {
            this.pdfPath = path;
            return this;
        }

        public Builder setOnPageClickListener(View.OnClickListener listener) {
            if (listener != null) {
                pageClickListener = listener;
            }
            return this;
        }

        public PDFPagerAdapter create() {
            PDFPagerAdapter adapter = new PDFPagerAdapter(context, pdfPath, errorHandler);
            adapter.pageClickListener = pageClickListener;
            return adapter;
        }
    }
}
