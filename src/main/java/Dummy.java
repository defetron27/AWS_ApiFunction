import com.google.cloud.translate.Language;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;

import java.util.List;

public class Dummy
{
    public static void main(String[] args)
    {
        Translate translate = TranslateOptions.getDefaultInstance().getService();

        List<Language> languages = translate.listSupportedLanguages();

        for (Language language : languages)
        {
            System.out.printf("Name: %s, Code: %s\n", language.getName(), language.getCode());
        }
    }
}
