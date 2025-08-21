package com.github.musicsnsproject.web.controller.view;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.github.musicsnsproject.common.security.userdetails.CustomUserDetails;
import com.github.musicsnsproject.service.mypage.eumpyo.EumpyoChargeService;
import com.github.musicsnsproject.service.mypage.eumpyo.EumpyoHistoryService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final EumpyoChargeService eumpyoChargeService;
    private final EumpyoHistoryService eumpyoHistoryService;

    // 모든 /mypage/* 뷰에 보유 코인 주입
    @ModelAttribute("myCoinBalance")
    public Long myCoinBalance(@AuthenticationPrincipal CustomUserDetails loginUser,
		   	   				  @RequestHeader(name = "X-Dev-UserId", required = false) Long devUserId)
			   				  {
    	
        // 로그인 사용자 확인
    //  Long userId = (loginUser != null ? loginUser.getUserId() : null);
        Long userId = (devUserId != null) ? devUserId : (loginUser != null ? loginUser.getUserId() : null);        
        
        if (userId == null) return 0L;
        Long coin = eumpyoChargeService.getUserCoin(userId); // users.coin 기준
        
        return (coin == null ? 0L : coin);
    }

    
    // 음표 충전 페이지
    @GetMapping("/eumpyo/charge")
    public String charge() {
        return "mypage/eumpyo/charge";
    }

    private int asInt(Object obj, int def) {
        if (obj == null) return def;
        if (obj instanceof Number) return ((Number) obj).intValue();
        try { return Integer.parseInt(String.valueOf(obj)); }
        catch (Exception ignore) { return def; }
    }

    
    // 음표 충전내역 페이지
    @GetMapping("/eumpyo/chargeHistory")
    public ModelAndView chargeHistoryPage(@AuthenticationPrincipal CustomUserDetails loginUser,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size,
            		   	   				  @RequestHeader(name = "X-Dev-UserId", required = false) Long devUserId,
                                          HttpServletRequest request) {

        ModelAndView mav = new ModelAndView("mypage/eumpyo/chargeHistory");

        page = Math.max(1, page);
        size = Math.max(1, Math.min(50, size));

        // 로그인 사용자 확인
    //  Long userId = (loginUser != null ? loginUser.getUserId() : null);
        Long userId = (devUserId != null) ? devUserId : (loginUser != null ? loginUser.getUserId() : null);    
        
        if (userId == null) {
            mav.addObject("list", null);
            mav.addObject("totalCount", 0);
            mav.addObject("currentShowPageNo", 1);
            mav.addObject("sizePerPage", size);
            mav.addObject("pageBar", "");
            
            return mav;
        }

        Map<String, Object> map = eumpyoHistoryService.getChargeHistory(userId, page, size);

        int totalCount        = asInt(map.get("totalCount"), 0);
        int sizePerPage       = asInt(map.getOrDefault("size", size), size);
        int currentShowPageNo = asInt(map.getOrDefault("page", page), page);

        int totalPage = (int) Math.ceil((double) totalCount / Math.max(1, sizePerPage));
        if (totalPage <= 0) totalPage = 1;
        if (currentShowPageNo > totalPage) currentShowPageNo = totalPage;
        if (currentShowPageNo < 1) currentShowPageNo = 1;

        mav.addObject("list", map.get("list"));
        mav.addObject("totalCount", totalCount);
        mav.addObject("currentShowPageNo", currentShowPageNo);
        mav.addObject("sizePerPage", sizePerPage);

        String baseUrl = request.getContextPath() + "/mypage/eumpyo/chargeHistory";
        String pageBar = (totalCount > 0)
                ? makePageBar(totalCount, sizePerPage, currentShowPageNo, baseUrl)
                : "";
        mav.addObject("pageBar", pageBar);

        return mav;
    }

    // 음표 구매내역 페이지
    @GetMapping("/eumpyo/purchaseHistory")
    public ModelAndView purchaseHistoryPage(@AuthenticationPrincipal CustomUserDetails loginUser,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestHeader(name = "X-Dev-UserId", required = false) Long devUserId,
                                            HttpServletRequest request) {

        ModelAndView mav = new ModelAndView("mypage/eumpyo/purchaseHistory");

        page = Math.max(1, page);
        size = Math.max(1, Math.min(50, size));

        // 로그인 사용자 확인
    //  Long userId = (loginUser != null ? loginUser.getUserId() : null);
        Long userId = (devUserId != null) ? devUserId : (loginUser != null ? loginUser.getUserId() : null);  
        
        if (userId == null) {
            mav.addObject("list", null);
            mav.addObject("totalCount", 0);
            mav.addObject("currentShowPageNo", 1);
            mav.addObject("sizePerPage", size);
            mav.addObject("pageBar", "");
            return mav;
        }

        Map<String, Object> map = eumpyoHistoryService.getPurchaseHistory(userId, page, size);

        int totalCount        = asInt(map.get("totalCount"), 0);
        int sizePerPage       = asInt(map.getOrDefault("size", size), size);
        int currentShowPageNo = asInt(map.getOrDefault("page", page), page);

        int totalPage = (int) Math.ceil((double) totalCount / Math.max(1, sizePerPage));
        if (totalPage <= 0) totalPage = 1;
        if (currentShowPageNo > totalPage) currentShowPageNo = totalPage;
        if (currentShowPageNo < 1) currentShowPageNo = 1;

        mav.addObject("list", map.get("list"));
        mav.addObject("totalCount", totalCount);
        mav.addObject("currentShowPageNo", currentShowPageNo);
        mav.addObject("sizePerPage", sizePerPage);

        String baseUrl = request.getContextPath() + "/mypage/eumpyo/purchaseHistory";
        String pageBar = (totalCount > 0)
                ? makePageBar(totalCount, sizePerPage, currentShowPageNo, baseUrl)
                : "";
        mav.addObject("pageBar", pageBar);

        return mav;
    }

    // 페이지바 생성
    private String makePageBar(int totalCount, int sizePerPage, int currentShowPageNo, String baseUrl) {
    	
        int totalPage = (int) Math.ceil((double) totalCount / sizePerPage);
        if (totalPage <= 0) totalPage = 1;

        final int blockSize = 5;

        int startNo = ((currentShowPageNo - 1) / blockSize) * blockSize + 1;
        int endNo   = Math.min(startNo + blockSize - 1, totalPage);

        boolean isFirstPage   = (currentShowPageNo <= 1);
        boolean isLastPage    = (currentShowPageNo >= totalPage);
        boolean showFirstLast = (currentShowPageNo > blockSize);  // 6페이지부터 « » 노출

        StringBuilder sb = new StringBuilder();
        sb.append("<ul class='pg-bar'>");

        // « 맨처음
        if (showFirstLast) {
            if (isFirstPage) {
                sb.append("<li class='pg-item pg-item--first is-disabled'>")
                  .append("<span class='pg-text pg-text--icon' aria-hidden='true'>&laquo;</span>")
                  .append("</li>");
            } else {
                sb.append("<li class='pg-item pg-item--first'>")
                  .append("<a class='pg-link pg-link--icon pg-first' aria-label='맨처음' href='")
                  .append(baseUrl).append("?page=1&size=").append(sizePerPage).append("'>")
                  .append("<span aria-hidden='true'>&laquo;</span></a></li>");
            }
        }

        // ‹ 이전
        if (isFirstPage) {
            sb.append("<li class='pg-item pg-item--prev is-disabled'>")
              .append("<span class='pg-text pg-text--icon' aria-hidden='true'>&lsaquo;</span>")
              .append("</li>");
        } else {
            sb.append("<li class='pg-item pg-item--prev'>")
              .append("<a class='pg-link pg-link--icon pg-prev' aria-label='이전' href='")
              .append(baseUrl).append("?page=").append(currentShowPageNo - 1)
              .append("&size=").append(sizePerPage).append("'>")
              .append("<span aria-hidden='true'>&lsaquo;</span></a></li>");
        }

        // 페이지 번호
        for (int pageNo = startNo; pageNo <= endNo; pageNo++) {
            if (pageNo == currentShowPageNo) {
                sb.append("<li class='pg-item pg-item--num is-current'>")
                  .append("<span class='pg-current' aria-current='page'>").append(pageNo).append("</span>")
                  .append("</li>");
            } else {
                sb.append("<li class='pg-item pg-item--num'>")
                  .append("<a class='pg-link' href='").append(baseUrl)
                  .append("?page=").append(pageNo)
                  .append("&size=").append(sizePerPage).append("'>").append(pageNo).append("</a></li>");
            }
        }

        // › 다음
        if (isLastPage) {
            sb.append("<li class='pg-item pg-item--next is-disabled'>")
              .append("<span class='pg-text pg-text--icon' aria-hidden='true'>&rsaquo;</span>")
              .append("</li>");
        } else {
            sb.append("<li class='pg-item pg-item--next'>")
              .append("<a class='pg-link pg-link--icon pg-next' aria-label='다음' href='")
              .append(baseUrl).append("?page=").append(currentShowPageNo + 1)
              .append("&size=").append(sizePerPage).append("'>")
              .append("<span aria-hidden='true'>&rsaquo;</span></a></li>");
        }

        // » 마지막
        if (showFirstLast) {
            if (isLastPage) {
                sb.append("<li class='pg-item pg-item--last is-disabled'>")
                  .append("<span class='pg-text pg-text--icon' aria-hidden='true'>&raquo;</span>")
                  .append("</li>");
            } else {
                sb.append("<li class='pg-item pg-item--last'>")
                  .append("<a class='pg-link pg-link--icon pg-last' aria-label='마지막' href='")
                  .append(baseUrl).append("?page=").append(totalPage)
                  .append("&size=").append(sizePerPage).append("'>")
                  .append("<span aria-hidden='true'>&raquo;</span></a></li>");
            }
        }

        sb.append("</ul>");
        return sb.toString();
    }
}
