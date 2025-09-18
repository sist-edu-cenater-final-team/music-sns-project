document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("sideSearchForm");
    const keyword = document.getElementById("sideSearchKeyword");

    form.addEventListener("submit", function (e) {
        if (keyword.value.trim() === "") {
            e.preventDefault();
            alert("검색어를 입력하세요!");
            keyword.focus();
        }
    });
    loadRecommendData();


});
// 추천 데이터 로드
async function loadRecommendData() {
    const keywordList = document.getElementById('keywordList');
    const trackList = document.getElementById('trackList');

    try {
        // 로딩 표시
        keywordList.innerHTML = '<div class="side-recommend-loading"><div class="side-loading-spinner"></div>로딩 중...</div>';
        trackList.innerHTML = '<div class="side-recommend-loading"><div class="side-loading-spinner"></div>로딩 중...</div>';

        const response = await fetch('/api/music/spotify/side-bar');
        const data = await response.json();

        if (data.success) {
            renderKeywords(data.success.responseData.trackNames);
            renderTracks(data.success.responseData.recommendSongs);
        }
    } catch (error) {
        console.error('추천 데이터 로드 실패:', error);
        keywordList.innerHTML = '<div class="side-recommend-loading">데이터를 불러올 수 없습니다.</div>';
        trackList.innerHTML = '<div class="side-recommend-loading">데이터를 불러올 수 없습니다.</div>';
    }
}

// 인기 검색어 렌더링
function renderKeywords(trackNames) {
    const keywordList = document.getElementById('keywordList');

    const keywordHTML = trackNames.map((keyword, index) => `
        <div class="side-keyword-item" onclick="searchByKeyword('${keyword}')">
            <span class="side-keyword-rank">${index + 1}</span>
            <span class="side-keyword-text">${keyword}</span>
        </div>
    `).join('');

    keywordList.innerHTML = keywordHTML;
}


// 추천 곡 렌더링
function renderTracks(tracks) {
    const trackList = document.getElementById('trackList');

    const trackHTML = tracks.map(track => {
        const artistsText = track.artists.map(artist =>
            `<a href="/music/artist/${artist.artistId}" class="side-artist-link">${artist.artistName}</a>`
        ).join(', ');

        return `
            <div class="side-track-item">
                <img src="${track.albumImageUrl}" alt="${track.trackName}" class="side-track-image"
                     onerror="this.src='/images/music/default-album.png'" onclick="goToPage('album','${track.albumId}')">
                <div class="side-track-info">
                    <div class="side-track-name" onclick="goToPage('track', '${track.trackSpotifyUrl}')">
                        <span class="side-track-name-span">
                            ${track.trackName}
                        </span>
                    </div>
                    <div class="side-track-artists">${artistsText}</div>
                </div>
            </div>
        `;
    }).join('');

    trackList.innerHTML = trackHTML;
}

function goToPage(pageType, value) {
    // 트랙 상세 페이지나 검색 결과로 이동
    switch (pageType) {
        case 'album':
            window.location.href = `${ctxPath}/music/album/${value}`;
            break;
        case 'artist':
            window.location.href = `${ctxPath}/music/artist/${value}`;
            break;
        case 'track':
            openSpotifyPopup(value);
            break;
    }
}
// 화면 절반 크기 팝업창으로 Spotify 페이지 열기
function openSpotifyPopup(url) {
    const screenWidth = window.screen.width;
    const screenHeight = window.screen.height;

    const popupWidth = Math.floor(screenWidth / 2);
    const popupHeight = Math.floor(screenHeight * 0.8);
    const left = Math.floor((screenWidth - popupWidth) / 2);
    const top = Math.floor((screenHeight - popupHeight) / 2);

    const features = `width=${popupWidth},height=${popupHeight},left=${left},top=${top},scrollbars=yes,resizable=yes`;

    window.open(url, 'spotifyPopup', features);
}


// 키워드로 검색
function searchByKeyword(keyword) {
    const form = document.getElementById('sideSearchForm');
    const keywordInput = document.getElementById('sideSearchKeyword');
    const categorySelect = document.getElementById('sideSearchCategory');

    keywordInput.value = keyword;
    categorySelect.value = 'all';
    form.submit();
}