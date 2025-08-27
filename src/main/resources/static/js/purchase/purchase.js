// api/purchaseMusic/list
document.addEventListener("DOMContentLoaded", function () {
    purchase.createList();
});

const purchase = {
    render : document.querySelector('#myPurchaseMusic'),
    createList : () => {
        // 스프링 시큐리티 인증 토큰을 헤더에 추가하여 주문 목록 요청
        return AuthFunc.apiRequest(() =>
            axios.get('/api/purchaseMusic/list', {
                headers: AuthFunc.getAuthHeader()
            })
        )
            .then(response => {
                console.log('구매한 상품 목록:', response.data);
                purchase.renderMusicList(response.data);
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
    renderMusicList : (musicData) => {
        let HTML = ``;
        musicData.forEach((item, index) => {
            HTML += `
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

        purchase.render.innerHTML = HTML;
    }
}