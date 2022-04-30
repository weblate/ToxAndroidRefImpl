/**
 * [TRIfA], Java part of Tox Reference Implementation for Android
 * Copyright (C) 2017 Zoff <zoff@zoff.cc>
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.
 */

package com.zoffcc.applications.trifa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.zoffcc.applications.trifa.HelperGroup.tox_group_by_confid__wrapper;
import static com.zoffcc.applications.trifa.MainActivity.tox_group_peer_get_name;
import static com.zoffcc.applications.trifa.MainActivity.tox_group_self_get_peer_id;
import static com.zoffcc.applications.trifa.MainActivity.tox_group_self_get_public_key;
import static com.zoffcc.applications.trifa.TrifaToxService.orma;

public class GroupInfoActivity extends AppCompatActivity
{
    static final String TAG = "trifa.GrpInfoActy";
    TextView this_group_id = null;
    EditText this_title = null;
    EditText group_myname_text = null;
    TextView this_privacy_state_text = null;
    TextView group_mypubkey_text = null;
    String group_id = "-1";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupinfo);

        Intent intent = getIntent();
        group_id = intent.getStringExtra("group_id");

        this_group_id = (TextView) findViewById(R.id.group_id_text);
        group_mypubkey_text = (TextView) findViewById(R.id.group_mypubkey_text);
        this_title = (EditText) findViewById(R.id.group_name_text);
        group_myname_text = (EditText) findViewById(R.id.group_myname_text);
        this_privacy_state_text = (TextView) findViewById(R.id.group_privacy_status_text);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if ((group_id == null) || (group_id.equals("-1")))
        {
            this_group_id.setText("*error*");
        }
        else
        {
            this_group_id.setText(group_id.toLowerCase());
        }
        this_title.setText("*error*");

        long group_num = -1;

        try
        {
            group_num = tox_group_by_confid__wrapper(group_id);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            group_myname_text.setText(tox_group_peer_get_name(group_num, tox_group_self_get_peer_id(group_num)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            group_mypubkey_text.setText(tox_group_self_get_public_key(group_num));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            this_title.setText(orma.selectFromGroupDB().
                    group_identifierEq(group_id.toLowerCase()).
                    toList().get(0).name);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        String privacy_state_text = "Unknown Group Privacy State";

        try
        {
            final int privacy_state = orma.selectFromGroupDB().
                    group_identifierEq(group_id.toLowerCase()).
                    toList().get(0).privacy_state;

            if (privacy_state == ToxVars.TOX_GROUP_PRIVACY_STATE.TOX_GROUP_PRIVACY_STATE_PUBLIC.value)
            {
                privacy_state_text = "Public Group";
            }
            else if (privacy_state == ToxVars.TOX_GROUP_PRIVACY_STATE.TOX_GROUP_PRIVACY_STATE_PRIVATE.value)
            {
                privacy_state_text = "Private (Invitation only) Group";
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this_privacy_state_text.setText(privacy_state_text);
    }
}
