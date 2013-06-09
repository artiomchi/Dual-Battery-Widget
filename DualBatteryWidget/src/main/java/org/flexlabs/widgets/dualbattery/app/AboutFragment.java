/*
 * Copyright 2013 Artiom Chilaru (http://flexlabs.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flexlabs.widgets.dualbattery.app;

import android.text.util.Linkify;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;

import org.flexlabs.widgets.dualbattery.R;

@EFragment(R.layout.about)
public class AboutFragment extends SherlockFragment {
    @ViewById(R.id.about_link_flexlabs) TextView link_flexlabs;
    @ViewById(R.id.about_link_googleplus) TextView link_googleplus;
    @ViewById(R.id.about_link_github) TextView link_github;
    @ViewById(R.id.about_link_translate) TextView link_translate;

    @AfterViews
    public void linkifyLinks() {
        Linkify.addLinks(link_flexlabs, Linkify.ALL);
        Linkify.addLinks(link_googleplus, Linkify.ALL);
        Linkify.addLinks(link_github, Linkify.ALL);
        Linkify.addLinks(link_translate, Linkify.ALL);
    }
}
