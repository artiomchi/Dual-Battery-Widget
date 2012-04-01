/*
 * Copyright 2012 Artiom Chilaru (http://flexlabs.org)
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

package org.flexlabs.widgets.dualbattery.widgetsettings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockFragment;
import org.flexlabs.widgets.dualbattery.MixPanelComponent;
import org.flexlabs.widgets.dualbattery.R;

public class SherlockFeedbackFragment extends SherlockFragment {
    private EditText feedbackEditor;
    private MixPanelComponent mMixPanel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feedback_form, null);
        if (mMixPanel == null) {
            mMixPanel = new MixPanelComponent(getActivity());
            mMixPanel.track(MixPanelComponent.BATTERY_INFO, null);
        }
        feedbackEditor = (EditText)view.findViewById(R.id.feedbackEditor);
        feedbackEditor.requestFocus();

        view.findViewById(R.id.sendFeedback).setOnClickListener(feedbackListener);
        return view;
    }

    private final View.OnClickListener feedbackListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FeedbackFragment.sendFeedback(mMixPanel, getActivity(), feedbackEditor.getText());
            feedbackEditor.setText(null);
        }
    };
}
