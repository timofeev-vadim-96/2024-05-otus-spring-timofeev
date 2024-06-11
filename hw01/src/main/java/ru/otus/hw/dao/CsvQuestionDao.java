package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        try {
            ClassPathResource resource = new ClassPathResource(fileNameProvider.getTestFileName());
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

            CsvToBean<QuestionDto> csvToBean = new CsvToBeanBuilder<QuestionDto>(reader)
                    .withSeparator(';')
                    .withType(QuestionDto.class)
                    .withSkipLines(1) // Пропускаем первую строку, содержащую комментарий
                    .build();

            // Использовать CsvToBean
            // https://opencsv.sourceforge.net/#collection_based_bean_fields_one_to_many_mappings
            // Использовать QuestionReadException
            // Про ресурсы: https://mkyong.com/java/java-read-a-file-from-resources-folder/
            return csvToBean.parse()
                    .stream()
                    .map(QuestionDto::toDomainObject)
                    .toList();
        } catch (IOException e) {
            throw new QuestionReadException(e.getMessage(), e);
        }
    }
}
