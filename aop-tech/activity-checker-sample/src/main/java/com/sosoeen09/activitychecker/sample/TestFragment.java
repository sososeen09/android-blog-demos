package com.sosoeen09.activitychecker.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TestFragment extends Fragment implements View.OnClickListener {

    TextView tvStatus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        view.findViewById(R.id.btn_activity_2).setOnClickListener(this);
        view.findViewById(R.id.btn_activity_3).setOnClickListener(this);
        view.findViewById(R.id.btn_activity_4).setOnClickListener(this);
        view.findViewById(R.id.btn_reset).setOnClickListener(this);
        tvStatus = view.findViewById(R.id.tv_show_status);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_activity_2:
                toActivity2();
                break;
            case R.id.btn_activity_3:
                toActivity3();
                break;
            case R.id.btn_activity_4:
                toActivity4();
                break;
            case R.id.btn_reset:
                StatusHolder.reset();
                setStatus();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setStatus();
    }

    private void setStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("登录: ");
        sb.append(StatusHolder.sHasLogin);
        sb.append("\n");

        sb.append("绑定手机号: ");
        sb.append(StatusHolder.sHasBindPhone);

        tvStatus.setText(sb);
    }

    private void toActivity2() {
        startActivity(new Intent(getContext(), Activity2.class));
    }

    private void toActivity3() {
        startActivity(new Intent(getContext(), Activity3.class));
    }

    private void toActivity4() {
        startActivity(new Intent(getContext(), Activity4.class));
    }
}
