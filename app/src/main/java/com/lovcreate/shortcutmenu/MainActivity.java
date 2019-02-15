package com.lovcreate.shortcutmenu;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    ShortcutManager mShortcutManager;
    @BindView(R.id.add)
    Button add;
    @BindView(R.id.disabled)
    Button disabled;
    @BindView(R.id.enabled)
    Button enabled;
    @BindView(R.id.remove)
    Button remove;
    @BindView(R.id.removeAll)
    Button removeAll;
    @BindView(R.id.content)
    EditText title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mShortcutManager = getSystemService(ShortcutManager.class);
    }

    @OnClick({R.id.add, R.id.disabled, R.id.enabled, R.id.remove})
    public void click(View button) {
        String content = title.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(MainActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (button.getId()) {
            case R.id.add: {
                addShortcutMenu(content);
                break;
            }
            case R.id.disabled: {
                disabledShortcutMenu(content);
                break;
            }
            case R.id.enabled: {
                enabledShortcutMenu(content);
                break;
            }
            case R.id.remove: {
                removeShortcutMenu(content);
                break;
            }
        }
    }
    @OnClick(R.id.removeAll)
    public void removeAll() {
        removeAllShortcutMenu();
    }

    //添加快捷菜单
    public void addShortcutMenu(String title) {
        //桌面快捷菜单只能显示4个
        //多添加会和栈一样 移除一个才能显示多添加的那个 容易引起bug 所以需要控制在4个之内 如果要添加新的要移除以前的
        if (mShortcutManager.getDynamicShortcuts().size() >= 4) {
            Toast.makeText(MainActivity.this, "菜单数量不能多于4个", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, ShortcutActivity.class);
        intent.setAction(Intent.ACTION_VIEW);

        ShortcutInfo info = new ShortcutInfo.Builder(this,
                new BigDecimal(Math.random()*100).setScale(0, BigDecimal.ROUND_HALF_UP).toPlainString())
                .setShortLabel(title)
                .setLongLabel("长标题 " + title)
                .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                .setIntent(intent)
                .setDisabledMessage("此功能被禁用")
                .build();
        if (mShortcutManager.addDynamicShortcuts(Collections.singletonList(info))) {
            Toast.makeText(MainActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
        }
    }
    //禁用快捷菜单 主要针对的是Pinning Shortcuts
    public void disabledShortcutMenu(String title) {
        List<ShortcutInfo> infoList = mShortcutManager.getPinnedShortcuts();
        for (ShortcutInfo info : infoList) {
            if (title.contentEquals(info.getShortLabel())) {
                Toast.makeText(MainActivity.this, "禁用的菜单id是：" + info.getId(), Toast.LENGTH_SHORT).show();
                mShortcutManager.disableShortcuts(Collections.singletonList(info.getId()));
            }
        }
    }
    //启用快捷菜单 主要针对的是Pinning Shortcuts
    public void enabledShortcutMenu(String title) {
        List<ShortcutInfo> infoList = mShortcutManager.getPinnedShortcuts();
        for (ShortcutInfo info : infoList) {
            if (title.contentEquals(info.getShortLabel())) {
                Toast.makeText(MainActivity.this, "启用的菜单id是：" + info.getId(), Toast.LENGTH_SHORT).show();
                mShortcutManager.enableShortcuts(Collections.singletonList(info.getId()));
                mShortcutManager.updateShortcuts(Collections.singletonList(info));
            }
        }
    }
    //移除指定快捷菜单
    public void removeShortcutMenu(String title) {
        List<ShortcutInfo> infoList = mShortcutManager.getDynamicShortcuts();
        for (ShortcutInfo info : infoList) {
            if (title.contentEquals(info.getShortLabel())) {
                Toast.makeText(MainActivity.this, "移除的菜单id是：" + info.getId(), Toast.LENGTH_SHORT).show();
                mShortcutManager.removeDynamicShortcuts(Collections.singletonList(info.getId()));
                mShortcutManager.disableShortcuts(Collections.singletonList(info.getId()));
            }
        }

    }
    //移除所有快捷菜单
    public void removeAllShortcutMenu() {
        mShortcutManager.removeAllDynamicShortcuts();
        List<ShortcutInfo> infoList = mShortcutManager.getPinnedShortcuts();
        if (infoList.size() > 0) {
            List<String> ids = infoList.stream().map(ShortcutInfo::getId).collect(Collectors.toList());
            mShortcutManager.disableShortcuts(ids);
        }
    }
}
