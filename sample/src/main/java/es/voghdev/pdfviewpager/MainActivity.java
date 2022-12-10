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
package es.voghdev.pdfviewpager;

import android.os.Bundle;
import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;

public class MainActivity extends BaseSampleActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.std_example);
        setContentView(R.layout.activity_main);

        ViewPager2 pdfViewPager = findViewById(R.id.pdfViewPager);

        String path = "/storage/emulated/0/Documents/doc.pdf";
        pdfViewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);

        View.OnClickListener clickListener = v -> {
        };

        pdfViewPager.setAdapter(new PDFPagerAdapter(this, clickListener, path));
    }
}
