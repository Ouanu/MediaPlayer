package com.moment.mobileplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.moment.mobileplayer.R;

public class TitleBar extends LinearLayout {

    private final Context context;
    private View search;
    private View game;
    private View iv_history;
    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        search = getChildAt(1);
        game = getChildAt(2);
        iv_history = getChildAt(3);

        search.setOnClickListener(new MyOnClickListener());
        game.setOnClickListener(new MyOnClickListener());
        iv_history.setOnClickListener(new MyOnClickListener());
    }

    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_search:
                    Toast.makeText(context, "Search", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.rl_game:
                    Toast.makeText(context, "Game", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.iv_history:
                    Toast.makeText(context, "History", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
