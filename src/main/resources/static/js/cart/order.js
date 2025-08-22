document.addEventListener("DOMContentLoaded", function () {
    order.createList();
});

const token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTU2NzQ1OTQsImV4cCI6NDkwOTI3NDU5NCwic3ViIjoiMjMiLCJyb2xlcyI6IlJPTEVfVVNFUiJ9.J2-HxxZZuEVrfQIjmPeujwehl6ExKDm8gdtae291uu4";
const order = {
    tbody : document.querySelector('#orderCartBody'),
    checkAll : document.querySelector('#cartAllCheck'),
    // 장바구니에서 세션에 저장한 cartIdLIst 꺼내기
    cartIdList : JSON.parse(sessionStorage.getItem("cartIdList")),
    renderOrderList : (cartData) => {
        //console.log('cartData:', cartData);

        let cartHTML = ``;

        cartData.forEach((item, index) => {
            cartHTML += `
                <tr>
                    <td scope="row">${index + 1}</td>
                    <td>
                        <div class="music-info">
                            <div class="music-img">
                                <img src="${item.albumImageUrl}" alt="노래 이미지" />
                            </div>
                            <p class="music-text">${item.musicName}</p>
                        </div>
                    </td>
                    <td>
                        <p class="music-artist">${item.artistName}</p>
                    </td>
                    <td>
                        <p class="music-artist">${item.albumName}</p>
                    </td>
                </tr>
            `;
        });


        order.tbody.innerHTML = cartHTML;
        document.querySelector("#orderTotalPrice").innerHTML = cartData.length;

    },
    createList : () => {

        if (order.cartIdList === null) {
            alert("주문할 상품이 없습니다.")
            location.href = `${ctxPath}/cart/list`;
            return;
        }

        // 스프링 시큐리티 인증 토큰을 헤더에 추가하여 주문 목록 요청
        fetch('/api/order?cartIdList='+order.cartIdList.join(","), {
            headers: { 'Authorization': token }
        })
            .then(res => res.json())
            .then(data => {
                // customMessage 값 사용
                if(data.error){
                    alert(data.error.customMessage);
                    return;
                }
                console.log("주문 목록:", data);
                order.renderOrderList(data);
            })
            .catch(err => console.error(err));
    },
    // 구매 확정
    confirm : () => {
        if(!confirm("정말 구매하시겠습니까?")) return;

        // 스프링 시큐리티 인증 토큰을 헤더에 추가하여 주문 요청
        fetch('/api/order/confirm', {
            method: 'POST',
            headers: { 'Authorization': token },
            body: new URLSearchParams({
                cartIdList : order.cartIdList
            })
        })
        .then(res => res.text())
        .then(data => {
            // customMessage 값 사용
            console.log("주문 결과:", data);
            if(data.error){
                alert(data.error.customMessage);
                return;
            }
            // 주문완료 후에 주문완료페이지로 이동
            alert(data);
            sessionStorage.removeItem("cartIdList"); // 주문 후 장바구니 비우기
            location.href = `${ctxPath}/order/complete`;
        })
        .catch(() => {
            alert("서버와 통신 중 오류가 발생했습니다.");
        });
    }
}

document.querySelector('.btn-order').addEventListener('click', order.confirm);

