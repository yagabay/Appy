package com.vismus.appy;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PointingTextView extends LinearLayout{

    public enum ViewColor{
        BLUE,
        GRAY
    }

    // views
    LinearLayout _layRoot;
    LinearLayout _layMargin;
    TextView _txvText;
    ImageView _imvTriangle;

    public PointingTextView(Context context, AttributeSet attrs){
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.pointing_text_view, this);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.com_vismus_appy_PointingTextView, 0, 0);
        try{
            // find views
            _layRoot = findViewById(R.id.lay_root);
            _layMargin = findViewById(R.id.lay_margin);
            _txvText = findViewById(R.id.txv_text);
            _imvTriangle = findViewById(R.id.imv_triangle);

            // get attrs
            Integer viewHeight = array.getInteger(R.styleable.com_vismus_appy_PointingTextView_view_height, 0);
            Integer viewColor = array.getInteger(R.styleable.com_vismus_appy_PointingTextView_view_color, 0);
            Integer direction = array.getInteger(R.styleable.com_vismus_appy_PointingTextView_view_direction, 0);
            String text = array.getString(R.styleable.com_vismus_appy_PointingTextView_text);
            Integer textSize = array.getInteger(R.styleable.com_vismus_appy_PointingTextView_text_size, 0);
            Integer textColor = array.getInteger(R.styleable.com_vismus_appy_PointingTextView_text_color, 0);
            Integer margin = array.getInteger(R.styleable.com_vismus_appy_PointingTextView_text_indent, 0);
            Integer triangleWidth = array.getInteger(R.styleable.com_vismus_appy_PointingTextView_triangle_width, 0);

            // init views
            _layRoot.setLayoutDirection(direction);
            _layRoot.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, viewHeight));
            _layMargin.setLayoutParams(new LinearLayout.LayoutParams(margin, ViewGroup.LayoutParams.MATCH_PARENT));
            _imvTriangle.setLayoutParams(new LinearLayout.LayoutParams(triangleWidth, ViewGroup.LayoutParams.MATCH_PARENT));
            _txvText.setText(text);
            _txvText.setTextSize(textSize);
            _txvText.setTextColor(textColor);
            _imvTriangle.setScaleX(direction == 0 ? 1 : -1);
            setViewColor(viewColor == 0 ? ViewColor.BLUE : ViewColor.GRAY);
        }
        finally{
            array.recycle();
        }
    }

    public void setText(String text){
        _txvText.setText(text);
    }

    public void setViewColor(ViewColor viewColor) {
        _txvText.setBackgroundResource(viewColor == ViewColor.BLUE ? R.color.blue : R.color.gray);
        _layMargin.setBackgroundResource(viewColor == ViewColor.BLUE ? R.color.blue : R.color.gray);
        _imvTriangle.setBackgroundResource(viewColor == ViewColor.BLUE ? R.drawable.right_pointing_triangle_blue : R.drawable.right_pointing_triangle_gray);
    }

}
