document.addEventListener("DOMContentLoaded", function () {
    order.createList();
});

const token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTU1ODczNTQsImV4cCI6MTc1NTU5MDk1NCwic3ViIjoiMjMiLCJyb2xlcyI6IlJPTEVfVVNFUiJ9.7SRttBXoDfWerjPyvmO90_a9U62Z7D5Hh80DFbx1EWY";
const order = {
    tbody : document.querySelector('#orderCartBody'),
    checkAll : document.querySelector('#cartAllCheck'),
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

    },
    createList : () => {
        // 장바구니에서 세션에 저장한 cartIdLIst 꺼내기
        const cartIdList = JSON.parse(sessionStorage.getItem("cartIdList"));

        // 스프링 시큐리티 인증 토큰을 헤더에 추가하여 주문 목록 요청
        fetch(`/api/cart/order?cartIdList=${cartIdList.join(",")}`, {
            headers: { 'Authorization': token }
        })
            .then(res => res.json())
            .then(data => {
                console.log("주문 목록:", data);
                order.renderOrderList(data); // 주문 상품 목록 렌더링 함수
            })
            .catch(err => console.error(err));
    }
}

