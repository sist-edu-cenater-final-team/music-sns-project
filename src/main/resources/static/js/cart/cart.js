const apiRequest = AuthFunc.apiRequest;//함수참조

document.addEventListener("DOMContentLoaded", function () {

    cart.createList();

});

const cart = {
    tbody : document.querySelector('#cartBody'),
    checkAll : document.querySelector('#cartAllCheck'),
    renderCart : (cartData) => {
        console.log('cartData:', cartData);

        let cartHTML = ``;
        // 장바구니가 비어있을 경우
        if(!cartData || cartData.length === 0) {
            cart.tbody.innerHTML = `<tr><td colspan="7">장바구니가 비어있습니다.</td></tr>`;
            return;
        }
        cartData.forEach((item, index) => {
            cartHTML += `
                <tr>
                    <td>
                        <label class="check-form">
                            <input type="checkbox" id="cartCheck${index + 1}" name="cartCheck" data-cart-id="${item.cartId}">
                            <span class="check"></span>
                        </label>
                    </td>
                    <td>${index + 1}</td>
                    <td class="link_td" onclick="window.open('https://open.spotify.com/track/${item.musicId}')">
                        <div class="music-info">
                            <div class="music-img">
                                <img src="${item.albumImageUrl}" alt="노래 이미지" />
                            </div>
                            <p>${item.musicName}</p>
                        </div>
                    </td>
                    <td class="link_td" onclick="window.open('https://open.spotify.com/artist/${item.artistId}')">
                        <p class="music-artist">${item.artistName}</p>
                    </td>
                    <td class="link_td" onclick="window.open('https://open.spotify.com/album/${item.albumId}')">
                        <p class="music-artist">${item.albumName}</p>
                    </td>
                    <td>
                        <p class="music-text">
                            <i class="ico-eumpyo"></i>
                            <span class="music-price">1</span>
                        </p>
                    </td>
                    <td>
                        <button type="button" class="btn-cart-delete" data-cart-id="${item.cartId}" onclick="cart.directDelete(this)"></button>
                    </td>
                </tr>
            `;
        });


        cart.tbody.innerHTML = cartHTML;

        document.querySelector("#musicCartCount").innerHTML = cartData.length;

        // 체크박스 이벤트 연결
        cart.initCheckEvents();
    },
    // 장바구니 리스트 API 요청
    createList : () => {
        return AuthFunc.apiRequest(() =>
                    axios.get(`${ctxPath}/api/cart/list`, {
                        headers: AuthFunc.getAuthHeader()
                    }))
                .then(response => {
                    // console.log("cartList:", response.data);
                    cart.renderCart(response.data);
                })
                .catch((error) => {
                    console.error('오류:', error);
                    if (error.response) {
                        const errorData = error.response.data.error;
                        if (errorData){
                            alert(errorData.customMessage);
                        }
                    }
                });
    },
    initCheckEvents: () => {
        const rowChecks = cart.tbody.querySelectorAll('input[name="cartCheck"]');

        // 전체 선택 클릭 시
        cart.checkAll.addEventListener('change', () => {
            rowChecks.forEach(item => item.checked = cart.checkAll.checked);
            cart.updateMasterState();
        });

        // 개별 체크박스 클릭 시
        rowChecks.forEach(item => {
            item.addEventListener('change', () => cart.updateMasterState());
        });

        // 초기 상태 반영
        cart.updateMasterState();
    },
    updateMasterState: () => {
        const rowChecks = cart.tbody.querySelectorAll('input[name="cartCheck"]');
        const checkedCount = cart.tbody.querySelectorAll('input[name="cartCheck"]:checked').length;

        document.querySelector("#musicCount").innerHTML = checkedCount;

        let musicPrice = 0;
        cart.tbody.querySelectorAll('input[name="cartCheck"]:checked').forEach(item => {
            musicPrice += Number(item.closest('tr').querySelector('.music-price').innerHTML);
        })
        document.querySelector("#musicPrice").innerHTML = musicPrice;

        if (checkedCount === 0) {
            cart.checkAll.checked = false;
            cart.checkAll.indeterminate = false;
        } else if (checkedCount === rowChecks.length) {
            cart.checkAll.checked = true;
            cart.checkAll.indeterminate = false;
        } else {
            cart.checkAll.checked = false;
            cart.checkAll.indeterminate = true;
        }
    },
    // 선택 삭제하기
    selectDelete : () => {
        // 받아온 cartId 배열 만들기
        const cartIdList = Array.from(cart.tbody.querySelectorAll('input[name="cartCheck"]:checked'))
            .map(cb => Number(cb.dataset.cartId));

        cart.deleteItem(1, cartIdList);
    },
    // 바로 삭제하기
    directDelete : (e) => {
        const cartId = [Number(e.dataset.cartId)];
        cart.deleteItem(0, cartId);
    },
    // 장바구니 삭제하기
    deleteItem : (status, cartIdList) => {
        console.log("cartIdList : " + cartIdList);
        // status 0 : 바로 삭제
        // ststus 1 : 선택 삭제
        if (status !== 0 && cart.tbody.querySelectorAll('input[name="cartCheck"]:checked').length < 1) {
            alert("삭제할 상품을 선택해주세요!");
            return;
        }
        if (!confirm('정말 상품을 삭제하시겠습니까?')) return;

        return apiRequest(() =>
            axios.delete('/api/cart/delete?cartIdList='+cartIdList, {
                headers: AuthFunc.getAuthHeader()
            })
        )
        .then(async (response) => {
            console.log("response : ", response);
            alert("상품을 삭제하였습니다.");

            cart.createList();
        })
        .catch(error => {
            console.log("삭제 error " + error);
            console.error('삭제 중 오류 발생:', error.message);
            alert('서버 또는 네트워크 오류입니다.');
        });
    },
    // 주문 요청하기
    order : () => {
        // 받아온 cartId 배열 만들기
        const cartIdList = Array.from(cart.tbody.querySelectorAll('input[name="cartCheck"]:checked'))
            .map(cb => Number(cb.dataset.cartId));

        // URLSearchParams 객체 만들기
        const params = new URLSearchParams();
        cartIdList.forEach(id => params.append("cartIdList", id));

        if (cart.tbody.querySelectorAll('input[name="cartCheck"]:checked').length < 1) {
            alert("주문할 상품을 선택해주세요!");
            return;
        }

        return apiRequest(() =>
                axios.post('/api/order/create?'+params.toString(), {}, {
                    headers: AuthFunc.getAuthHeader(),
                })
            )
            .then( (response) => {
                console.log("order response : ", response);

                // 선택한 cartIdList를 sessionStorage에 저장하기
                sessionStorage.setItem("cartIdList", JSON.stringify(cartIdList));

                // 주문 미리보기페이지 이동
                location.href = `${ctxPath}/order/preview`;

            })
            .catch(error => {
                console.error('오류:', error);
                if (error.response) {
                    const errorData = error.response.data.error;
                    if (errorData) {
                        if (error.response.status === 401) {
                            // 인증 오류 처리 (예: 로그인 페이지로 리다이렉트)
                            alert('로그인이 필요합니다. 로그인 페이지로 이동합니다.');
                            location.href = `${ctxPath}/auth/login`;
                            return;
                        } else {
                            alert(errorData.customMessage);
                        }
                    } else {
                        alert('서버 또는 네트워크 오류입니다.');
                    }
                }
            });

    }
}



// 선택 삭제
document.querySelector('.btn-delete')?.addEventListener('click', cart.selectDelete);
// 주문하러가기
document.querySelector('.btn-order')?.addEventListener('click', cart.order);