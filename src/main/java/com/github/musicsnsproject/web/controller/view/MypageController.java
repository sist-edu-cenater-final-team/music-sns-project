package com.github.musicsnsproject.web.controller.view;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/mypage")
public class MypageController {

    // 장바구니
    @GetMapping("cart")
    public String musicCart(){
        return "mypage/cart";
    }

    // 결제페이지
    @GetMapping("payment")
    public String payment(){
        return "mypage/payment";
    }


    // 음표 충전 페이지
    @GetMapping("/eumpyo/sellingList")
    public String sellingList() {
        return "mypage/eumpyo/sellingList";
    }

    // 음표 사용내역 페이지
    @GetMapping("/eumpyo/purchasedProductList")
    public String purchasedProductList() {
        return "mypage/eumpyo/purchasedProductList";
    }

    // 음표 충전 페이지
    @GetMapping("/eumpyo/purchasedList")
    public String purchasedList() {
        return "mypage/eumpyo/purchasedList";
    }

/*
    // 음표 사용내역 페이지
    @GetMapping("/eumpyo/purchasedProductList")
    public String purchasedProductList(@RequestParam(value="pageno",    defaultValue="1") int currentShowPageNo,
            						   Model model,
            						   HttpServletRequest request, HttpServletResponse response) {

		int sizePerPage = 5;

		int totalPage = 0;        // 전체 페이지 개수
		long totalDataCount = 0;  // 전체 데이터의 개수
		String pageBar = "";      // 페이지바

		try {
			Page<purchasedProductListServiceService> pagePurchaseHistory = purchasedProductListServiceService.getPurchaseHistory(currentShowPageNo, sizePerPage);

			totalPage = pagePurchaseHistory.getTotalPages(); // 전체 페이지수 개수
		//	System.out.println("~~~ 확인용 전체 페이지수 개수 : " + totalPage);

			if(currentShowPageNo > totalPage) {
				currentShowPageNo = totalPage;
				pagePurchaseHistory = purchasedProductListServiceService.getPurchaseHistory(currentShowPageNo, sizePerPage);
			}

			totalDataCount = pagePurchaseHistory.getTotalElements(); // 전체 데이터의 개수
		//	System.out.println("~~~ 확인용 전체 데이터의 개수 : " + totalDataCount);

			List<PurchaseHistory> PurchaseHistoryList = pagePurchaseHistory.getContent(); // 현재 페이지의 데이터 목록

			// 현재 페이지의 데이터 목록인 List<PurchaseHistory> 를 List<PurchaseHistoryDTO> 로 변환한다.
			List<PurchaseHistoryDTO> purchaseHistoryDtoList = purchaseHistoryList.stream()
													         .map(PurchaseHistory::toDTO)
												             .collect(Collectors.toList());

			for(BoardDTO dto : boardDtoList) {
				System.out.println("~~~ 확인용 글제목 : " + dto.getSubject());
			}

			model.addAttribute("boardDtoList", purchaseHistoryDtoList);

			// ================ 페이지바 만들기 시작 ====================== //

			int blockSize = 10;

			int loop = 1;

			int pageno = ((currentShowPageNo - 1)/blockSize) * blockSize + 1;


			pageBar = "<ul style='list-style:none;'>";
			String url = "/board/list";

			// === [맨처음][이전] 만들기 === //
			if(pageno != 1) {
				pageBar += "<li style='display:inline-block; width:70px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&pageno=1'>[맨처음]</a></li>";
				pageBar += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&pageno="+(pageno-1)+"'>[이전]</a></li>";
			}

			while( !(loop > blockSize || pageno > totalPage) ) {

				if(pageno == currentShowPageNo) {
					pageBar += "<li style='display:inline-block; width:30px; font-size:12pt; border:solid 1px gray; color:red; padding:2px 4px;'>"+pageno+"</li>";
				}
				else {
					pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&pageno="+pageno+"'>"+pageno+"</a></li>";
				}

				loop++;
				pageno++;
			}// end of while------------------------

			// === [다음][마지막] 만들기 === //
			if(pageno <= totalPage) {
				pageBar += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&pageno="+pageno+"'>[다음]</a></li>";
				pageBar += "<li style='display:inline-block; width:70px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&pageno="+totalPage+"'>[마지막]</a></li>";
			}

			pageBar += "</ul>";

			model.addAttribute("pageBar", pageBar);

			// ================ 페이지바 만들기 끝 ====================== //

			model.addAttribute("totalDataCount", totalDataCount); // 페이징 처리시 보여주는 순번을 나타내기 위한 것임.
			model.addAttribute("currentShowPageNo", currentShowPageNo); // 페이징 처리시 보여주는 순번을 나타내기 위한 것임.
			model.addAttribute("sizePerPage", sizePerPage); // 페이징 처리시 보여주는 순번을 나타내기 위한 것임.

			Cookie cookie = new Cookie("listURL", listURL);

			cookie.setMaxAge(24*60*60);  // 쿠키수명은 1일로 함
			cookie.setPath("/eumpyo/");  // 쿠키가 브라우저에서 전송될 URL 경로 범위(Path)를 지정하는 설정임

			response.addCookie(cookie);

		} catch(Exception e) {

		}

		return "mypage/eumpyo/purchasedProductList";

    }


    // 음표 충전내역 페이지
    @GetMapping("/eumpyo/purchasedList")
    public String purchasedList(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        Long userId = user.getUserId();
        model.addAttribute("purchasedList", purchaseHistoryService.getPurchasedList(userId));
        return "mypage/eumpyo/purchasedList";
    }
*/
}
