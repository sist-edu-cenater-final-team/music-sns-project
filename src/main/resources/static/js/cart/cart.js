document.addEventListener("DOMContentLoaded", function () {
    cart.createList();


    // 선택 삭제
    document.querySelector('.btn-delete').addEventListener('click', cart.selectDelete);

    // 결제하러가기
    document.querySelector('.btn-payment').addEventListener('click', cart.payment);

    // 장바구니 추가하기
    //document.querySelector('.btn-cart-add').addEventListener('click', cart.add);
});
const userId = 23;

const cart = {
    tbody : document.querySelector('#cartBody'),
    checkAll : document.querySelector('#cartAllCheck'),
    renderCart : (cartData) => {
        console.log('cartData:', cartData);
        let cartHTML = ``;
        cartData.forEach( (item, index) => {
            cartHTML += `
                <tr>
                    <td>
                        <input type="checkbox" id="cartCheck${index+1}" name="cartCheck" data-cart-id="${item.cartId}">
                    </td>
                    <td scope="row">${index+1}</td>
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
                    <td>
                        <p class="music-text"><span class="music-price">1</span>음표</p>
                    </td>
                    <td>
                        <button type="button" class="btn-cart-delete" data-cart-id="${item.cartId}" onclick="cart.directDelete(this)"></button>
                    </td>
                </tr>
            `;
        });

        cart.tbody.innerHTML = cartHTML;

        // 체크박스 이벤트 연결
        cart.initCheckEvents();
    },
    createList : () => {
        fetch(`/api/cart/list?userId=${encodeURIComponent(userId)}`)
        .then(response => response.json())
        .then(data => {
            //console.log('cart list:', data);
            cart.renderCart(data);
        })
        .catch(error => {
            console.error('Error:', error);
            alert('장바구니 목록 조회 중 오류가 발생했습니다.');
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
    // 선택된 cartId 배열 반환
    getSelectedCartIds: () => {
        return Array.from(cart.tbody.querySelectorAll('input[name="cartCheck"]:checked'))
            .map(cb => cb.dataset.cartId);
    },
    selectDelete : () => {
        const cartId = document.querySelector("#cartId");
        cartId.value = cart.getSelectedCartIds();

        //
        const cartIdList = cartId.value.split(',');

        cart.deleteItem(1, cartIdList);
    },
    // 바로 삭제하기
    directDelete : (e) => {
        const cartId = e.dataset.cartId;
        cart.deleteItem(0, cartId);
        console.log(cartId);
    },
    add : () => {
        fetch('/api/cart/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                userId: userId,
                trackId: trackid
            })
        })
            .then(response => response.json())
            .then(data => {
                console.log('cart add:', data);
                alert('장바구니에 담았습니다.');

                cart.createList();
            })
            .catch(error => {
                console.error('Error:', error);
                alert('장바구니 추가 중 오류가 발생했습니다.');
            });
    },
    deleteItem : (status, cartId) => {
        // status 0 : 바로 삭제
        // ststus 1 : 선택 삭제
        if (status !== 0 && cart.tbody.querySelectorAll('input[name="cartCheck"]:checked').length < 1) {
            alert("삭제할 상품을 선택해주세요!");
            return;
        }
        if (!confirm('정말 상품을 삭제하시겠습니까?')) return;

        fetch('/api/cart/delete', {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userId: userId,
                cartId: cartId
            })
        })
        .then(async (response) => {
            console.log("response : ", response);
            console.log("response.status : ", response.status);
            const msg = await response.text();

            if (response.status !== 200) {
                alert("상품 삭제에 실패하였습니다."+ msg);
                return;
            }
            alert("상품을 삭제하였습니다.");

            cart.createList();
        })
        .catch(error => {
            console.error('삭제 중 오류 발생:', error.message);
            alert('서버 또는 네트워크 오류입니다.');
        });
    },
    payment : () => {

    }
}