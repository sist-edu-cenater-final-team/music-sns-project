package com.github.musicsnsproject.repository.jpa.emotion;


import com.github.musicsnsproject.common.converter.custom.EmotionConverter;
import com.github.musicsnsproject.common.myenum.EmotionEnum;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Check;

import java.util.List;


@Entity
@Table(name = "emotion")
@Getter
@Check(constraints = "emotion_value IN ('평온','우울','사랑','기쁨','피곤','화남')")
public class Emotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emotionId;

    @Column(unique = true)
    @Convert(converter = EmotionConverter.class)
    private EmotionEnum emotionValue;

    @OneToMany(mappedBy = "emotion", fetch = FetchType.LAZY)
    private List<UserEmotion> userEmotions;

    public static Emotion fromEmotionValue(EmotionEnum emotionValue) {
        Emotion emotion = new Emotion();
        emotion.emotionId = switch (emotionValue) {
            case CALM -> 1L;
            case LOVE -> 3L;
            case SAD -> 2L;
            case ANGRY -> 6L;
            case HAPPY -> 4L;
            case TIRED -> 5L;
        };
        return emotion;
    }


}
