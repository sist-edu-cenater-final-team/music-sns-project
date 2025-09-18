package com.github.musicsnsproject.service.account;

import com.github.musicsnsproject.common.exceptions.CustomBadRequestException;
import com.github.musicsnsproject.common.exceptions.CustomServerException;
import com.github.musicsnsproject.repository.jpa.account.user.MyUserRepository;
import com.github.musicsnsproject.repository.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.nurigo.sdk.message.exception.NurigoEmptyResponseException;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.exception.NurigoUnknownException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static com.github.musicsnsproject.common.MyUtils.generateRandomNumber;

@Service
@RequiredArgsConstructor
@Log4j2
public class PhoneVerifyService {

    private final DefaultMessageService defaultMessageService;
    private final RedisRepository redisRepository;
    @Value("${cool-sms.phone-number}")
    private String serverPhoneNumber;
    private final MyUserRepository myUserRepository;

    private Message createMessage(String to, String code) {
        String messageText = "[MusicSNS] 인증번호는 " + code + " 입니다. 10분 이내로 입력해주세요.";
        Message message = new Message();
        message.setFrom(serverPhoneNumber);
        message.setTo(to);
        message.setText(messageText);
        return message;
    }

    private void sendExceptionHandling(Message message) {
        try {
            defaultMessageService.send(message);
        } catch (NurigoMessageNotReceivedException e) {
            log.warn(e.getMessage(), e);
            throw CustomServerException.of()
                    .systemMessage(e.getMessage())
                    .customMessage("수신 가능한 번호를 입력 해주세요.")
                    .build();
        } catch (NurigoEmptyResponseException e) {
            log.warn(e.getMessage(), e);
            throw CustomServerException.of()
                    .customMessage("문자 발송 실패 (빈 응답)")
                    .build();
        } catch (NurigoUnknownException e) {
            log.warn(e.getMessage(), e);
            throw CustomServerException.of()
                    .customMessage("문자 발송 실패 (알 수 없는 에러)")
                    .build();
        }
    }
    private String replacePhoneNumber(String phoneNumber) {
        return phoneNumber.replace(" ", "").replace("-", "");
    }

    public void sendMessage(String phoneNumber) {
        String to = replacePhoneNumber(phoneNumber);
        String verifyCode = String.valueOf(generateRandomNumber());
        Message message = createMessage(to, verifyCode);
        //레디스에 저장
        redisRepository.save(to, verifyCode, Duration.ofMinutes(10));
        //발송시작
        sendExceptionHandling(message);
    }

    public boolean verifyCode(String phoneNumber, String code) {
        String to = replacePhoneNumber(phoneNumber);
        String serverCode = redisRepository.getValue(to);
        if(serverCode == null)
            throw CustomBadRequestException.of()
                    .customMessage("인증 코드가 만료되었습니다. 다시 시도해주세요.")
                    .build();
        return serverCode.equals(code);
    }

    @Transactional(readOnly = true)
    public boolean duplicateCheckPhoneNumber(String phoneNumber) {
        return !myUserRepository.existsByPhoneNumber(phoneNumber);
    }
}
