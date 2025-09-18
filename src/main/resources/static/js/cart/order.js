document.addEventListener("DOMContentLoaded", function () {
    order.createList();
});

const order = {
    tbody : document.querySelector('#orderCartBody'),
    checkAll : document.querySelector('#cartAllCheck'),
    // 장바구니에서 세션에 저장한 cartIdLIst 배열로 변환해서 꺼내기
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
        return AuthFunc.apiRequest(() =>
                    axios.get('/api/order/list?cartIdList='+order.cartIdList.join(","), {
                        headers: AuthFunc.getAuthHeader()
                    })
                )
                .then(response => {
                    console.log('주문할 상품 목록:', response.data);
                    order.renderOrderList(response.data);
                })
            .catch(error => {
                console.error('오류:', error);
                if (error.response) {
                    const errorData = error.response.data.error;
                    if (errorData){
                        alert(errorData.customMessage);
                    }
                }
            });
    },
    // 구매 확정하기
    confirm : () => {
        if(!confirm("정말 구매하시겠습니까?")) return;

        // 스프링 시큐리티 인증 토큰을 헤더에 추가하여 주문 요청
        return AuthFunc.apiRequest(() =>
                    axios.post('/api/order/confㅔirm?cartIdList='+order.cartIdList.join(","), {}, {
                        headers: AuthFunc.getAuthHeader(),
                    })
                )
                .then(response => {
                    console.log("주문 완료:", response);
                    alert("구매가 완료되었습니다.");

                    // 주문 후 장바구니 페이지로 이동
                    sessionStorage.removeItem("cartIdList");
                    location.href = `${ctxPath}/order/complete`;
                })
                .catch((error) => {
                    if(error.response?.data?.error?.customMessage){
                        alert(error.response.data.error.customMessage);
                    }
                    console.error('오류:', error);
                });
    }
}

document.querySelector('.btn-order').addEventListener('click', order.confirm);

