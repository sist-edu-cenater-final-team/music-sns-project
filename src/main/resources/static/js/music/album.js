const albumId = window.location.pathname.split('/').pop();

document.addEventListener("DOMContentLoaded", function () {
    axios.get(`${ctxPath}/api/music/spotify/album?albumId=${albumId}`)
        .then(response => {
            const responseData = response.data.success.responseData;
            renderAlbumImageAndColors(responseData.album.albumImageUrl);
            renderAlbumInfo(responseData);
            renderAlbumPopularity(responseData.popularity);
            renderTrackList(responseData.tracks);

        })
        .catch(error => {
            console.error("앨범 정보를 불러오지 못했습니다:", error);
        });
});
function renderAlbumInfo(responseData){
    const album = responseData.album;
    const albumNameElement = document.querySelector('.album-name');
    const albumTypeElement = document.querySelector('.album-type');
    const albumArtistsElement = document.querySelector('.album-artists');
    const albumGenresElement = document.querySelector('.album-genres');
    const albumReleaseDateElement = document.querySelector('.album-release-date');
    const albumDurationElement = document.querySelector('.album-duration');
    const albumLabelElement = document.querySelector('.album-label');
    const spotifyLinkElement = document.querySelector('.spotify-link');

    albumNameElement.textContent = album.albumName;
    albumTypeElement.textContent = album.albumType;
    const artistsTag = album.artists.map(artist => `<a href="${ctxPath}/music/artist/${artist.artistId}">${artist.artistName}</a>`).join(', ');
    albumArtistsElement.innerHTML = artistsTag;
    albumGenresElement.textContent = responseData.genres.length > 0 ? album.genres.join(', ') : '';
    albumReleaseDateElement.textContent = album.releaseDate;
    spotifyLinkElement.href = album.albumSpotifyUrl;
    // const labelIcon = getLabelIcon(responseData.label);
    albumLabelElement.innerHTML = `<i class="bi bi-disc"></i> ${responseData.label}`;
    spotifyLinkElement.innerHTML = `<i class="bi bi-spotify"></i>&nbsp Spotify`;
    spotifyLinkElement.style.display = "block";

    const totalDurationMs = responseData.tracks.reduce((sum, track) => sum + track.durationMs, 0)
    const totalTracks = responseData.tracks.length;
    const totalDurationMinutes = Math.floor(totalDurationMs / 60000);
    const totalDurationSeconds = Math.floor((totalDurationMs % 60000) / 1000);
    albumDurationElement.textContent = `${totalTracks}곡, ${totalDurationMinutes}분 ${totalDurationSeconds}초`;
}
function renderAlbumImageAndColors(albumImageUrl) {
    const albumImageElement = document.querySelector('.album-image');
    const loadingSpinner = document.querySelector('.loading-spinner');
    // 로딩 상태 시작
    albumImageElement.classList.add("loading");
    loadingSpinner.classList.remove("hidden");

    // 이미지 로드 완료 후 색상 추출
    albumImageElement.onload = async function() {
        try {
            const colors = await extractImageColors(this);
            applyGradientBackground(colors);
        } catch (error) {
            console.error("색상 추출 실패:", error);
        } finally {
            // 로딩 상태 종료
            albumImageElement.classList.remove("loading");
            loadingSpinner.classList.add("hidden");
        }
    };
    // 이미지 로드 실패 시 처리
    albumImageElement.onerror = function() {
        albumImageElement.classList.remove("loading");
        loadingSpinner.classList.add("hidden");
    };
    // CORS 문제 방지를 위해 crossOrigin 설정
    if (albumImageUrl) {
        albumImageElement.crossOrigin = "anonymous";
    }
    albumImageElement.src = albumImageUrl || `${ctxPath}/images/music/album/default-album.png`;


}

// 이미지에서 주요 색상 추출 함수
function extractImageColors(imageElement) {
    return new Promise((resolve) => {
        const canvas = document.createElement('canvas');
        const ctx = canvas.getContext('2d');

        canvas.width = 100;
        canvas.height = 100;

        ctx.drawImage(imageElement, 0, 0, 100, 100);

        const imageData = ctx.getImageData(0, 0, 100, 100);
        const data = imageData.data;

        let r = 0, g = 0, b = 0;
        let pixelCount = 0;

        // 이미지의 평균 색상 계산
        for (let i = 0; i < data.length; i += 4) {
            r += data[i];
            g += data[i + 1];
            b += data[i + 2];
            pixelCount++;
        }

        r = Math.floor(r / pixelCount);
        g = Math.floor(g / pixelCount);
        b = Math.floor(b / pixelCount);

        // 밝기 조절된 색상들 생성
        const lightColor = `rgba(${Math.min(r + 30, 255)}, ${Math.min(g + 30, 255)}, ${Math.min(b + 30, 255)}, 0.2)`;
        const darkColor = `rgba(${Math.max(r - 20, 0)}, ${Math.max(g - 20, 0)}, ${Math.max(b - 20, 0)}, 0.5)`;

        resolve({ lightColor, darkColor });
    });
}
//레이블별 아이콘 매핑
function getLabelIcon(labelName) {
    const labelIcons = {
        'Universal Music': 'bi bi-music-note-beamed',
        'Sony Music': 'bi bi-headphones',
        'Warner Music': 'bi bi-vinyl',
        'YG Entertainment': 'bi bi-star-fill',
        'SM Entertainment': 'bi bi-heart-fill',
        'JYP Entertainment': 'bi bi-lightning-fill'
    };

    // 레이블명에 키워드가 포함되어 있으면 해당 아이콘 반환
    for (const [key, icon] of Object.entries(labelIcons)) {
        if (labelName.includes(key)) {
            return icon;
        }
    }
    return 'bi bi-disc'; // 기본 아이콘
}

// 배경 그라데이션 적용 함수
function applyGradientBackground(colors) {
    const artistProfile = document.querySelector('.album-container');
    const gradient = `linear-gradient(180deg, ${colors.lightColor} 0%, ${colors.lightColor} 50%, ${colors.darkColor} 100%)`;
    artistProfile.style.background = gradient;
}
function renderAlbumPopularity(popularity) {
    const bar = document.querySelector(".popularity-bar .progress-bar");
    bar.style.width = popularity + "%";
    bar.setAttribute("aria-valuenow", popularity);
    bar.textContent = popularity + "%";

    // 색상 단계 (50 이하: 빨강, 51~75: 주황, 76~100: 초록)
    if (popularity <= 50) {
        bar.classList.remove("bg-success");
        bar.classList.add("bg-danger");
    } else if (popularity <= 75) {
        bar.classList.remove("bg-success");
        bar.classList.add("bg-warning");
    }
}
//트랙 랜더
function groupTracksByDisc(tracks) {
    const discGroups = new Map();
    let currentDisc = 1;
    let lastTrackNumber = 0;

    tracks.forEach(track => {
        // 트랙 번호가 이전 트랙보다 작거나 같으면 새로운 디스크
        if (track.trackNumber <= lastTrackNumber && track.trackNumber === 1) {
            currentDisc++;
        }

        if (!discGroups.has(currentDisc)) {
            discGroups.set(currentDisc, []);
        }

        discGroups.get(currentDisc).push(track);
        lastTrackNumber = track.trackNumber;
    });

    return discGroups;
}
function renderTrackList(tracks) {
    const trackListBody = document.querySelector('.track-list-body');

    // 디스크별로 트랙 그룹핑
    const discGroups = groupTracksByDisc(tracks);

    let trackListHTML = '';

    discGroups.forEach((discTracks, discNumber) => {
        // 멀티 디스크인 경우에만 디스크 헤더 표시
        if (discGroups.size > 1) {
            trackListHTML += `
                <div class="disc-header">
                    <h4><i class="bi bi-vinyl-fill"></i> DISC ${discNumber}</h4>
                </div>
            `;
        }

        // 해당 디스크의 트랙들 렌더링
        const trackItems = discTracks.map(track => {
            const artistsTag = track.artists.map(artist =>
                `<a href="${ctxPath}/music/artist/${artist.artistId}">${artist.artistName}</a>`
            ).join(', ');

            const durationFormatted = formatDuration(track.durationMs);

            return `
                <div class="track-item" data-track-id="${track.trackId}">
                    <div class="track-number">${track.trackNumber}</div>
                    <div class="track-name">
                        <span>
                            ${track.trackName}
                        </span>
                    </div>
                    <div class="track-artist">${artistsTag}</div>
                    <div class="track-duration">${durationFormatted}</div>
                    <div class="track-actions">
                        <button class="add-to-cart-btn" onclick="addToCart('${track.trackId}')" title="장바구니에 추가">
                            <i class="bi bi-cart-plus"></i>
                        </button>
                    </div>
                </div>
            `;
        }).join('');

        trackListHTML += trackItems;
    });

    trackListBody.innerHTML = trackListHTML;
    // 트랙 네임에 클릭 이벤트 추가
    const trackNames = document.querySelectorAll('.track-name');
    trackNames.forEach(trackName => {
        trackName.addEventListener('click', function() {
            const trackId = this.closest('.track-item').getAttribute('data-track-id');
            const spotifyUrl = `https://open.spotify.com/track/${trackId}`;
            openSpotifyPopup(spotifyUrl);
        });
    })
}

function formatDuration(durationMs) {
    const minutes = Math.floor(durationMs / 60000);
    const seconds = Math.floor((durationMs % 60000) / 1000);
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
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




function addToCart(trackId) {
    // 장바구니 추가 로직 구현
    console.log('장바구니에 추가:', trackId);
    // TODO: 실제 장바구니 API 호출
}


