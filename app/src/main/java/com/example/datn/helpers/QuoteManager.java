package com.example.datn.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class QuoteManager {
    private static final String PREFS_NAME = "QuotePrefs";
    private static final String USED_QUOTES_KEY = "UsedQuotes";
    private static final String LAST_DATE_KEY = "LastDate";

    private final List<String> quotesList;
    private final Set<String> usedQuotesSet;
    private String lastDate;
    private final SharedPreferences sharedPreferences;

    public QuoteManager(Context context) {
        // Khởi tạo danh sách các câu nói
        quotesList = new ArrayList<>();
        quotesList.add("''Vấp ngã không phải là thất bại, chỉ là dừng lại cho đỡ mỏi chân thôi!''");
        quotesList.add("''Đi chậm mà chắc, miễn là không đứng lại.''");
        quotesList.add("''Đừng bao giờ từ bỏ giấc mơ của mình.''");
        quotesList.add("''Thành công là kết quả của sự kiên trì.''");
        quotesList.add("''Hôm nay phải cố gắng hơn hôm qua.''");
        quotesList.add("''Nỗ lực hôm nay sẽ mang lại thành quả ngày mai.''");
        quotesList.add("''Hãy học như thể bạn sẽ sống mãi mãi, và sống như thể hôm nay là ngày cuối cùng.''");
        quotesList.add("''Thành công không phải là đích đến, mà là hành trình.''");
        quotesList.add("''Không có con đường tắt nào dẫn đến nơi nào xứng đáng.''");
        quotesList.add("''Người thành công là người biết học hỏi từ những người khác.''");
        quotesList.add("''Hãy bước ra khỏi vùng an toàn của bạn, nơi có điều kỳ diệu chờ đón bạn.''");
        quotesList.add("''Mỗi bài học đều là một viên gạch xây dựng cho tương lai.''");
        quotesList.add("''Chỉ cần bạn không từ bỏ, bạn sẽ đạt được điều bạn muốn.''");
        quotesList.add("''Hãy biến khó khăn thành cơ hội để trưởng thành.''");
        quotesList.add("''Học tập là một cuộc hành trình, không phải là một đích đến.''");
        quotesList.add("''Dù bạn đi chậm, nhưng miễn là bạn không ngừng lại.''");
        quotesList.add("''Thành công đến từ sự chuẩn bị và nỗ lực không ngừng.''");
        quotesList.add("''Hãy dám mơ ước lớn và làm việc chem chỉ để biến ước mơ thành hiện thực.''");
        quotesList.add("''Mỗi khó khăn đều là một cơ hội để bạn phát triển.''");
        quotesList.add("''Hãy sống như một người học hỏi, không phải như một người biết hết mọi thứ.''");
        quotesList.add("''Kiến thức là sức mạnh, và học tập là chìa khóa mở ra cánh cửa thành công.''");
        quotesList.add("''Chúng ta học hỏi không phải để biết, mà để biết cách sống.''");
        quotesList.add("''Không có gì là vô nghĩa nếu bạn biết cách học hỏi từ nó.''");
        quotesList.add("''Thái độ tích cực sẽ giúp bạn vượt qua mọi thử thách.''");
        quotesList.add("''Hãy yêu thích những gì bạn học, và bạn sẽ không phải làm việc một ngày nào trong đời.''");

        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Lấy dữ liệu từ SharedPreferences
        lastDate = sharedPreferences.getString(LAST_DATE_KEY, "");
        usedQuotesSet = sharedPreferences.getStringSet(USED_QUOTES_KEY, new HashSet<>());
    }

    public String getQuoteForToday() {
        // Lấy ngày hiện tại
        String currentDate = getCurrentDate();

        // Kiểm tra nếu ngày thay đổi
        if (!currentDate.equals(lastDate)) {
            lastDate = currentDate;

            // Nếu tất cả câu nói đã được sử dụng, khởi tạo lại danh sách
            if (usedQuotesSet.size() == quotesList.size()) {
                usedQuotesSet.clear();
            }

            // Chọn một câu nói ngẫu nhiên từ danh sách còn lại
            String newQuote = getRandomQuote();
            if (newQuote != null) {
                usedQuotesSet.add(newQuote);

                // Lưu trữ lại dữ liệu vào SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(LAST_DATE_KEY, lastDate);
                editor.putStringSet(USED_QUOTES_KEY, usedQuotesSet);
                editor.apply();

                return newQuote;
            }
        } else if (!usedQuotesSet.isEmpty()) {
            // Nếu ngày không thay đổi, lấy câu nói cũ
            return new ArrayList<>(usedQuotesSet).get(usedQuotesSet.size() - 1);
        }

        return null;
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        return day + "/" + month + "/" + year;
    }

    private String getRandomQuote() {
        List<String> availableQuotes = new ArrayList<>(quotesList);
        availableQuotes.removeAll(usedQuotesSet);

        if (!availableQuotes.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(availableQuotes.size());
            return availableQuotes.get(randomIndex);
        }
        return null;
    }
}
