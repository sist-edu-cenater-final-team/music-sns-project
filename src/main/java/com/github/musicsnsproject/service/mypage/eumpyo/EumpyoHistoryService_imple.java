package com.github.musicsnsproject.service.mypage.eumpyo;

import com.github.musicsnsproject.repository.mybatis.dao.eumpyo.EumpyoHistoryDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EumpyoHistoryService_imple implements EumpyoHistoryService {

    private final EumpyoHistoryDAO dao;

    @Override
    public Map<String, Object> getChargeHistory(long userId, int page, int size) {
        int totalCount = dao.countChargeHistory(userId);
        int totalPage  = (int) Math.ceil((double) totalCount / size);
        if (totalPage == 0) totalPage = 1;

        int current = Math.max(1, Math.min(page, totalPage));
        int offset  = (current - 1) * size;

        List<Map<String, Object>> list = dao.findChargeHistoryPage(userId, offset, size);

        Map<String, Object> out = new HashMap<>();
        out.put("result",      "success");
        out.put("list",        list);
        out.put("totalCount",  totalCount);
        out.put("totalPage",   totalPage);
        out.put("page",        current);
        out.put("size",        size);
        out.put("pageBar",     makePageBar(totalCount, size, current, "/mypage/eumpyo/history/charge"));
        return out;
    }

    @Override
    public Map<String, Object> getUseHistory(long userId, int page, int size) {
        int totalCount = dao.countUseHistory(userId);
        int totalPage  = (int) Math.ceil((double) totalCount / size);
        if (totalPage == 0) totalPage = 1;

        int current = Math.max(1, Math.min(page, totalPage));
        int offset  = (current - 1) * size;

        List<Map<String, Object>> list = dao.findUseHistoryPage(userId, offset, size);

        Map<String, Object> out = new HashMap<>();
        out.put("result",      "success");
        out.put("list",        list);
        out.put("totalCount",  totalCount);
        out.put("totalPage",   totalPage);
        out.put("page",        current);
        out.put("size",        size);
        out.put("pageBar",     makePageBar(totalCount, size, current, "/mypage/eumpyo/history/use"));
        return out;
    }

    // === 수업 BoardController 페이지바 스타일 ===
    private String makePageBar(int totalCount, int sizePerPage, int currentShowPageNo, String baseUrl) {
        int totalPage = (int) Math.ceil((double) totalCount / sizePerPage);
        if (totalPage == 0) totalPage = 1;

        int blockSize = 10;
        int loop = 1;
        int pageNo = ((currentShowPageNo - 1) / blockSize) * blockSize + 1;

        StringBuilder pageBar = new StringBuilder("<ul style='list-style:none;'>");

        // [맨처음][이전]
        pageBar.append("<li style='display:inline-block; width:70px; font-size:12pt;'><a href='")
               .append(baseUrl).append("?page=1&size=").append(sizePerPage)
               .append("'>[맨처음]</a></li>");
        if (pageNo != 1) {
            pageBar.append("<li style='display:inline-block; width:50px; font-size:12pt;'><a href='")
                   .append(baseUrl).append("?page=").append(pageNo - 1).append("&size=").append(sizePerPage)
                   .append("'>[이전]</a></li>");
        }

        // 페이지 번호들
        while (!(loop > blockSize || pageNo > totalPage)) {
            if (pageNo == currentShowPageNo) {
                pageBar.append("<li style='display:inline-block; width:30px; font-size:12pt; border:solid 1px gray; color:red; padding:2px 4px;'>")
                       .append(pageNo).append("</li>");
            } else {
                pageBar.append("<li style='display:inline-block; width:30px; font-size:12pt;'><a href='")
                       .append(baseUrl).append("?page=").append(pageNo).append("&size=").append(sizePerPage)
                       .append("'>").append(pageNo).append("</a></li>");
            }
            loop++; pageNo++;
        }

        // [다음][마지막]
        if (pageNo <= totalPage) {
            pageBar.append("<li style='display:inline-block; width:50px; font-size:12pt;'><a href='")
                   .append(baseUrl).append("?page=").append(pageNo).append("&size=").append(sizePerPage)
                   .append("'>[다음]</a></li>");
        }
        pageBar.append("<li style='display:inline-block; width:70px; font-size:12pt;'><a href='")
               .append(baseUrl).append("?page=").append(totalPage).append("&size=").append(sizePerPage)
               .append("'>[마지막]</a></li>");
        pageBar.append("</ul>");

        return pageBar.toString();
    }
}
