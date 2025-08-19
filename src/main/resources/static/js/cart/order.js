document.addEventListener("DOMContentLoaded", function () {
    order.createList();
});
//const userId = 23;

const order = {
    tbody : document.querySelector('#orderCartBody'),
    checkAll : document.querySelector('#cartAllCheck'),
    renderOrderList : (cartData) => {
        //console.log('cartData:', cartData);

        let cartHTML = ``;
        // 장바구니가 비어있을 경우
        if(cartData.length === 0) {
            order.tbody.innerHTML = `<tr><td colspan="7">장바구니가 비어있습니다.</td></tr>`;
            return;
        }
        cartData.forEach((item, index) => {
            cartHTML += `
                <tr>
                    <td>
                        <input type="checkbox" id="cartCheck${index + 1}" name="cartCheck" data-cart-id="${item.cartId}">
                    </td>
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

        // 체크박스 이벤트 연결
        order.initCheckEvents();
    },
    createList : () => {
        // 장바구니에서 세션에 저장한 cartIdLIst 꺼내기
        const cartIdList = JSON.parse(sessionStorage.getItem("cartIdList"));
        // userId 토큰 꺼내기
        const userId = localStorage.getItem("userId");

        // console.log("cartIdList:", `${cartIdList.join(",")}`);

        // 스프링 시큐리티 인증 토큰을 헤더에 추가하여 주문 목록 요청
        fetch(`/api/cart/order?cartIdList=${cartIdList.join(",")}`, {
            headers: { 'Authorization': userId },
        })
            .then(res => res.json())
            .then(data => {
                console.log("주문 목록:", data);
                order.renderOrderList(data); // 주문 상품 목록 렌더링 함수
            })
            .catch(err => console.error(err));
    },
    initCheckEvents: () => {
        const rowChecks = order.tbody.querySelectorAll('input[name="cartCheck"]');

        // 전체 선택 클릭 시
        order.checkAll.addEventListener('change', () => {
            rowChecks.forEach(item => item.checked = order.checkAll.checked);
            order.updateMasterState();
        });

        // 개별 체크박스 클릭 시
        rowChecks.forEach(item => {
            item.addEventListener('change', () => order.updateMasterState());
        });

        // 초기 상태 반영
        order.updateMasterState();
    },
    updateMasterState: () => {
        const rowChecks = order.tbody.querySelectorAll('input[name="cartCheck"]');
        const checkedCount = order.tbody.querySelectorAll('input[name="cartCheck"]:checked').length;

        //document.querySelector("#musicCount").innerHTML = checkedCount;

        // let musicPrice = 0;
        // order.tbody.querySelectorAll('input[name="cartCheck"]:checked').forEach(item => {
        //     musicPrice += Number(item.closest('tr').querySelector('.music-price').innerHTML);
        // })
        // document.querySelector("#musicPrice").innerHTML = musicPrice;

        // if (checkedCount === 0) {
        //     cart.checkAll.checked = false;
        //     cart.checkAll.indeterminate = false;
        // } else if (checkedCount === rowChecks.length) {
        //     cart.checkAll.checked = true;
        //     cart.checkAll.indeterminate = false;
        // } else {
        //     cart.checkAll.checked = false;
        //     cart.checkAll.indeterminate = true;
        // }
    }
}

