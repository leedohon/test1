package lab.nicc.kioskyoungcheon;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MinHyeong on 2018-03-24.
 */

public class KoreanKeyboard extends Fragment {
    List<Boolean> inputFlow = new ArrayList<>();
    char cho[] = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};
    char joong[] = {'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'};
    char jong[] = {' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};

    boolean upperStatus = false;

    String full = "";

    Context context;

    static TextView enterText;
    static ProgressBar searchPb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.korean_keyboard, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        enterText = (TextView) view.findViewById(R.id.enter_text);
        searchPb = (ProgressBar) view.findViewById(R.id.search_pb);

        context = MainActivity.context;

        view.findViewById(R.id.enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchPb.getVisibility() == View.VISIBLE)
                    return;
                Intent keyIntent = new Intent("keyboardEnterInput");
                context.sendBroadcast(keyIntent);
            }
        });

        final Button ㅃ = (Button) view.findViewById(R.id.ㅃ);
        final Button ㅉ = (Button) view.findViewById(R.id.ㅉ);
        final Button ㄸ = (Button) view.findViewById(R.id.ㄸ);
        final Button ㄲ = (Button) view.findViewById(R.id.ㄲ);
        final Button ㅆ = (Button) view.findViewById(R.id.ㅆ);
        final Button ㅒ = (Button) view.findViewById(R.id.ㅒ);
        final Button ㅖ = (Button) view.findViewById(R.id.ㅖ);
        final Button ㅂ = (Button) view.findViewById(R.id.ㅂ);
        final Button ㅈ = (Button) view.findViewById(R.id.ㅈ);
        final Button ㄷ = (Button) view.findViewById(R.id.ㄷ);
        final Button ㄱ = (Button) view.findViewById(R.id.ㄱ);
        final Button ㅅ = (Button) view.findViewById(R.id.ㅅ);

        Button ㅁ = (Button) view.findViewById(R.id.ㅁ);
        Button ㄴ = (Button) view.findViewById(R.id.ㄴ);
        Button ㅇ = (Button) view.findViewById(R.id.ㅇ);
        Button ㄹ = (Button) view.findViewById(R.id.ㄹ);
        Button ㅎ = (Button) view.findViewById(R.id.ㅎ);
        Button ㅋ = (Button) view.findViewById(R.id.ㅋ);
        Button ㅌ = (Button) view.findViewById(R.id.ㅌ);
        Button ㅊ = (Button) view.findViewById(R.id.ㅊ);
        Button ㅍ = (Button) view.findViewById(R.id.ㅍ);

        Button ㅛ = (Button) view.findViewById(R.id.ㅛ);
        Button ㅕ = (Button) view.findViewById(R.id.ㅕ);
        Button ㅑ = (Button) view.findViewById(R.id.ㅑ);
        final Button ㅐ = (Button) view.findViewById(R.id.ㅐ);
        final Button ㅔ = (Button) view.findViewById(R.id.ㅔ);
        Button ㅗ = (Button) view.findViewById(R.id.ㅗ);
        Button ㅓ = (Button) view.findViewById(R.id.ㅓ);
        Button ㅏ = (Button) view.findViewById(R.id.ㅏ);
        Button ㅣ = (Button) view.findViewById(R.id.ㅣ);
        Button ㅠ = (Button) view.findViewById(R.id.ㅠ);
        Button ㅜ = (Button) view.findViewById(R.id.ㅜ);
        Button ㅡ = (Button) view.findViewById(R.id.ㅡ);

        final View.OnClickListener jaeumListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputFlow.add(true);

                getInputKey((String) ((Button) v).getText());

                ㅃ.setVisibility(View.GONE);
                ㅉ.setVisibility(View.GONE);
                ㄸ.setVisibility(View.GONE);
                ㄲ.setVisibility(View.GONE);
                ㅆ.setVisibility(View.GONE);
                ㅒ.setVisibility(View.GONE);
                ㅖ.setVisibility(View.GONE);

                ㅂ.setVisibility(View.VISIBLE);
                ㅈ.setVisibility(View.VISIBLE);
                ㄷ.setVisibility(View.VISIBLE);
                ㄱ.setVisibility(View.VISIBLE);
                ㅅ.setVisibility(View.VISIBLE);
                ㅐ.setVisibility(View.VISIBLE);
                ㅔ.setVisibility(View.VISIBLE);

                upperStatus = false;
            }
        };

        View.OnClickListener moeumListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputFlow.add(false);

                getInputKey((String) ((Button) v).getText());

                ㅃ.setVisibility(View.GONE);
                ㅉ.setVisibility(View.GONE);
                ㄸ.setVisibility(View.GONE);
                ㄲ.setVisibility(View.GONE);
                ㅆ.setVisibility(View.GONE);
                ㅒ.setVisibility(View.GONE);
                ㅖ.setVisibility(View.GONE);

                ㅂ.setVisibility(View.VISIBLE);
                ㅈ.setVisibility(View.VISIBLE);
                ㄷ.setVisibility(View.VISIBLE);
                ㄱ.setVisibility(View.VISIBLE);
                ㅅ.setVisibility(View.VISIBLE);
                ㅐ.setVisibility(View.VISIBLE);
                ㅔ.setVisibility(View.VISIBLE);

                upperStatus = false;
            }
        };

        ㅂ.setOnClickListener(jaeumListener);
        ㅈ.setOnClickListener(jaeumListener);
        ㄷ.setOnClickListener(jaeumListener);
        ㄱ.setOnClickListener(jaeumListener);
        ㅅ.setOnClickListener(jaeumListener);
        ㅁ.setOnClickListener(jaeumListener);
        ㄴ.setOnClickListener(jaeumListener);
        ㅇ.setOnClickListener(jaeumListener);
        ㄹ.setOnClickListener(jaeumListener);
        ㅎ.setOnClickListener(jaeumListener);
        ㅋ.setOnClickListener(jaeumListener);
        ㅌ.setOnClickListener(jaeumListener);
        ㅊ.setOnClickListener(jaeumListener);
        ㅍ.setOnClickListener(jaeumListener);

        ㅃ.setOnClickListener(jaeumListener);
        ㅉ.setOnClickListener(jaeumListener);
        ㄸ.setOnClickListener(jaeumListener);
        ㄲ.setOnClickListener(jaeumListener);
        ㅆ.setOnClickListener(jaeumListener);


        ㅛ.setOnClickListener(moeumListener);
        ㅕ.setOnClickListener(moeumListener);
        ㅑ.setOnClickListener(moeumListener);
        ㅐ.setOnClickListener(moeumListener);
        ㅔ.setOnClickListener(moeumListener);
        ㅗ.setOnClickListener(moeumListener);
        ㅓ.setOnClickListener(moeumListener);
        ㅏ.setOnClickListener(moeumListener);
        ㅣ.setOnClickListener(moeumListener);
        ㅠ.setOnClickListener(moeumListener);
        ㅜ.setOnClickListener(moeumListener);
        ㅡ.setOnClickListener(moeumListener);

        ㅒ.setOnClickListener(moeumListener);
        ㅖ.setOnClickListener(moeumListener);

        final Button upper = (Button) view.findViewById(R.id.upper);
        upper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upperStatus = !upperStatus;
                SpannableString spanString = new SpannableString("↑");
                if (upperStatus) {
                    spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);

                    ㅃ.setVisibility(View.VISIBLE);
                    ㅉ.setVisibility(View.VISIBLE);
                    ㄸ.setVisibility(View.VISIBLE);
                    ㄲ.setVisibility(View.VISIBLE);
                    ㅆ.setVisibility(View.VISIBLE);
                    ㅒ.setVisibility(View.VISIBLE);
                    ㅖ.setVisibility(View.VISIBLE);

                    ㅂ.setVisibility(View.GONE);
                    ㅈ.setVisibility(View.GONE);
                    ㄷ.setVisibility(View.GONE);
                    ㄱ.setVisibility(View.GONE);
                    ㅅ.setVisibility(View.GONE);
                    ㅐ.setVisibility(View.GONE);
                    ㅔ.setVisibility(View.GONE);
                } else {
                    spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);

                    ㅃ.setVisibility(View.GONE);
                    ㅉ.setVisibility(View.GONE);
                    ㄸ.setVisibility(View.GONE);
                    ㄲ.setVisibility(View.GONE);
                    ㅆ.setVisibility(View.GONE);
                    ㅒ.setVisibility(View.GONE);
                    ㅖ.setVisibility(View.GONE);

                    ㅂ.setVisibility(View.VISIBLE);
                    ㅈ.setVisibility(View.VISIBLE);
                    ㄷ.setVisibility(View.VISIBLE);
                    ㄱ.setVisibility(View.VISIBLE);
                    ㅅ.setVisibility(View.VISIBLE);
                    ㅐ.setVisibility(View.VISIBLE);
                    ㅔ.setVisibility(View.VISIBLE);
                }
                upper.setText(spanString);
            }
        });

        Button undo = (Button) view.findViewById(R.id.undo);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent keyIntent = new Intent("keyboardInput");
                try {
                    inputFlow.clear();
                    full = full.substring(0, full.length() - 1);
                    keyIntent.putExtra("strings", full);
                    context.sendBroadcast(keyIntent);
                } catch (StringIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    keyIntent.putExtra("close", -1);
                    context.sendBroadcast(keyIntent);
                }
            }
        });
        Button closeButton = (Button)view.findViewById(R.id.close_keyboard);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.sendBroadcast(new Intent("keyboardInput").putExtra("close", -1));
            }
        });
    }

    String ch = "";
    String prev = "";

    private void getInputKey(String input) {
        if (inputFlow.size() == 2 && inputFlow.get(0) == true && inputFlow.get(1) == false) {
            prev = full.substring(full.length() - 1);
            full = full.substring(0, full.length() - 1);
            ch += input;
            full += makeChar(ch);
        } else if (inputFlow.size() == 3 && inputFlow.get(0) == true && inputFlow.get(1) == false && inputFlow.get(2) == true) {
            prev = full.substring(full.length() - 1);
            full = full.substring(0, full.length() - 1);
            ch += input;
            char tmp = makeChar(ch);
            full += tmp;
        } else if (inputFlow.size() == 3 && inputFlow.get(0) == true && inputFlow.get(1) == false && inputFlow.get(2) == false) {
            prev = full.substring(full.length() - 1);
            full = full.substring(0, full.length() - 1);
            ch += input;
            char tmp = makeChar(ch);
            full += tmp;
        } else if (inputFlow.size() == 4 && inputFlow.get(0) == true && inputFlow.get(1) == false && inputFlow.get(2) == true && inputFlow.get(3) == true) {
            prev = full.substring(full.length() - 1);
            full = full.substring(0, full.length() - 1);
            ch += input;
            char tmp = makeChar(ch);
            full += tmp;
        } else if (inputFlow.size() == 4 && inputFlow.get(0) == true && inputFlow.get(1) == false && inputFlow.get(2) == true && inputFlow.get(3) == false) {
            full = full.substring(0, full.length() - 1);
            full += prev;
            ch = String.valueOf(ch.charAt(ch.length() - 1));
            ch += input;
            inputFlow.clear();
            inputFlow.add(true);
            inputFlow.add(false);
            full += makeChar(ch);
        } else if (inputFlow.size() == 4 && inputFlow.get(0) == true && inputFlow.get(1) == false && inputFlow.get(2) == false && inputFlow.get(3) == true) {
            prev = full.substring(full.length() - 1);
            full = full.substring(0, full.length() - 1);
            ch += input;
            full += makeChar(ch);
        } else if (inputFlow.size() == 5 && inputFlow.get(0) == true && inputFlow.get(1) == false && inputFlow.get(2) == false && inputFlow.get(3) == true && inputFlow.get(4) == false) {
            full = full.substring(0, full.length() - 1);
            full += prev;
            ch = String.valueOf(ch.charAt(ch.length() - 1));
            ch += input;
            inputFlow.clear();
            inputFlow.add(true);
            inputFlow.add(false);
            full += makeChar(ch);
        } else if (inputFlow.size() == 5 && inputFlow.get(0) == true && inputFlow.get(1) == false && inputFlow.get(2) == true && inputFlow.get(3) == true && inputFlow.get(4) == false) {
            full = full.substring(0, full.length() - 1);
            full += prev;
            ch = String.valueOf(ch.charAt(ch.length() - 1));
            ch += input;
            inputFlow.clear();
            inputFlow.add(true);
            inputFlow.add(false);
            full += makeChar(ch);
        } else {
            ch = "";
            ch += input;
            full += ch;

            boolean tmp = false;
            if (inputFlow.get(inputFlow.size() - 1)) tmp = true;
            if (inputFlow.size() != 1) {
                inputFlow.clear();
                if (tmp) inputFlow.add(true);
            }

        }

        Intent keyIntent = new Intent("keyboardInput");
        keyIntent.putExtra("strings", full);
        context.sendBroadcast(keyIntent);
    }

    private char makeChar(String input) {
        char c = 'ㄱ';
        char j = 'ㅏ';
        char jj = ' ';

        boolean next = false, prevDoubled = false, failed = false;
        try {
            c = input.charAt(0);
            j = input.charAt(1);
            jj = input.charAt(2);

            if (input.length() >= 3) {
                if (input.charAt(1) == 'ㅗ') {
                    switch (input.charAt(2)) {
                        case 'ㅏ':
                            j = 'ㅘ';
                            break;
                        case 'ㅐ':
                            j = 'ㅙ';
                            break;
                        case 'ㅣ':
                            j = 'ㅚ';
                            break;
                        default:
                            failed = true;
                            break;
                    }
                } else if (input.charAt(1) == 'ㅜ') {
                    switch (input.charAt(2)) {
                        case 'ㅓ':
                            j = 'ㅝ';
                            break;
                        case 'ㅔ':
                            j = 'ㅞ';
                            break;
                        case 'ㅣ':
                            j = 'ㅟ';
                            break;
                        default:
                            failed = true;
                            break;
                    }
                } else if (input.charAt(1) == 'ㅡ') {
                    switch (input.charAt(2)) {
                        case 'ㅣ':
                            j = 'ㅢ';
                            break;
                        default:
                            failed = true;
                            break;
                    }
                } else if (input.charAt(2) == 'ㅃ' || input.charAt(2) == 'ㅉ' || input.charAt(2) == 'ㄸ') {
                    inputFlow.clear();
                    inputFlow.add(true);
                    ch = input.substring(input.length() - 1);

                    full += prev;
                    return ch.charAt(0);
                } else if (input.charAt(1) == 'ㅏ' || input.charAt(1) == 'ㅓ' || input.charAt(1) == 'ㅕ' || input.charAt(1) == 'ㅑ' || input.charAt(1) == 'ㅛ'
                        || input.charAt(1) == 'ㅠ' || input.charAt(1) == 'ㅣ') {
                    failed = true;
                } else {
                    if (failed) {
                        inputFlow.clear();
                        inputFlow.add(true);
                        ch = input.substring(input.length() - 1);

                        full += prev;
                        return ch.charAt(0);
                    }
                }
                if (input.length() == 4 && !inputFlow.get(2)) {
                    jj = input.charAt(3);
                    prevDoubled = true;
                }
                if (!inputFlow.get(2) && failed) {
                    inputFlow.clear();
                    inputFlow.add(false);
                    ch = input.substring(input.length() - 1);

                    full += prev;
                    return ch.charAt(0);
                }
            }
            if (input.length() == 4) {
                if (input.charAt(2) == 'ㄱ') {
                    switch (input.charAt(3)) {
                        case 'ㅅ':
                            jj = 'ㄳ';
                            break;
                        default:
                            next = true;
                            break;
                    }
                } else if (input.charAt(2) == 'ㄴ') {
                    switch (input.charAt(3)) {
                        case 'ㅈ':
                            jj = 'ㄵ';
                            break;
                        case 'ㅎ':
                            jj = 'ㄶ';
                            break;
                        default:
                            next = true;
                            break;
                    }
                } else if (input.charAt(2) == 'ㄹ') {
                    switch (input.charAt(3)) {
                        case 'ㄱ':
                            jj = 'ㄺ';
                            break;
                        case 'ㅁ':
                            jj = 'ㄻ';
                            break;
                        case 'ㅂ':
                            jj = 'ㄼ';
                            break;
                        case 'ㅅ':
                            jj = 'ㄽ';
                            break;
                        case 'ㅌ':
                            jj = 'ㄾ';
                            break;
                        case 'ㅎ':
                            jj = 'ㅀ';
                            break;
                        default:
                            next = true;
                            break;
                    }
                } else if (input.charAt(2) == 'ㅂ') {
                    switch (input.charAt(3)) {
                        case 'ㅅ':
                            jj = 'ㅄ';
                            break;
                        default:
                            next = true;
                            break;
                    }
                } else {
                    if (!prevDoubled) {
                        inputFlow.clear();
                        inputFlow.add(true);
                        ch = input.substring(input.length() - 1);

                        full += prev;
                        return ch.charAt(0);
                    }
                }
                if (next) {
                    inputFlow.clear();
                    inputFlow.add(true);
                    ch = input.substring(input.length() - 1);

                    full += prev;
                    return ch.charAt(0);
                }
                Log.e("zz", "");
            }
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        int choCode = -1, joongCode = -1, jongCode = 0;
        for (int i = 0; i < cho.length; i++) {
            if (c == cho[i])
                choCode = i;
        }
        for (int i = 0; i < joong.length; i++) {
            if (j == joong[i])
                joongCode = i;
        }
        for (int i = 0; i < jong.length; i++) {
            if (jj == jong[i])
                jongCode = i;
        }

        if (choCode == -1 && joongCode != -1) {
            inputFlow.clear();
            ch = input.substring(input.length() - 1);

            full += prev;
            return ch.charAt(0);
        }

        return (char) ((choCode * 588) + (joongCode * 28) + jongCode + 0xAC00);
    }
}