document.addEventListener("DOMContentLoaded", function() {
    //마지막 경로변수 artistId 가져오기
    // 예시: const artistId = window.location.pathname.split('/').pop();
    const artistId = window.location.pathname.split('/').pop();
    axios.get(`${ctxPath}/api/music/spotify/artist?artistId=${artistId}`)
        .then(response => {
            const artist = response.data.success.responseData;

            const artistImage = document.querySelector(".artist-image");
            const loadingSpinner = document.querySelector(".loading-spinner");

            // 로딩 상태 시작
            artistImage.classList.add("loading");
            loadingSpinner.classList.remove("hidden");

            // 이미지 로드 완료 후 색상 추출
            artistImage.onload = async function() {
                try {
                    const colors = await extractImageColors(this);
                    applyGradientBackground(colors);
                } catch (error) {
                    console.error("색상 추출 실패:", error);
                } finally {
                    // 로딩 상태 종료
                    artistImage.classList.remove("loading");
                    loadingSpinner.classList.add("hidden");
                }
            };
            // 이미지 로드 실패 시 처리
            artistImage.onerror = function() {
                artistImage.classList.remove("loading");
                loadingSpinner.classList.add("hidden");
            };
            // CORS 문제 방지를 위해 crossOrigin 설정
            if (artist.artistImageUrl) {
                artistImage.crossOrigin = "anonymous";
            }
            artistImage.src = artist.artistImageUrl || ctxPath+"/images/music/singer/singer-crop.png";


            document.querySelector(".artist-name").textContent = artist.artist.artistName;
            document.querySelector(".artist-genres").textContent = artist.artistGenres.join(", ");
            document.querySelector(".artist-followers").textContent = `팔로워: ${artist.totalFollowers.toLocaleString()}`;
            document.querySelector(".spotify-link").href = artist.artist.artistSpotifyUrl;

            // 인기도 퍼센트 반영
            const popularity = artist.artistPopularity;
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
        })
        .catch(error => {
            console.error("아티스트 정보를 불러오는 중 오류 발생:", error);
        });

//     앨범목록
    let currentPage = 1;
    const size = 8;

    async function fetchAlbums(page) {
        const loadMoreBtn = document.getElementById("load-more");
        const albumList = document.getElementById("album-list");

        // 로딩 스피너 생성 및 표시
        let loadingContainer = document.querySelector('.album-loading-container');
        if (!loadingContainer) {
            loadingContainer = document.createElement('div');
            loadingContainer.className = 'album-loading-container';
            loadingContainer.innerHTML = '<div class="album-loading-spinner"></div>';
        }
        // 첫 번째 로딩인지 더보기 로딩인지 구분
        if (page === 1) {
            // 첫 번째 로딩: 앨범 리스트 내부에 추가
            albumList.appendChild(loadingContainer);
        } else {
            // 더보기 로딩: 더보기 버튼 바로 위에 추가
            albumList.parentNode.insertBefore(loadingContainer, loadMoreBtn);
        }
        loadingContainer.classList.remove('hidden');

        // 더보기 버튼을 보이지 않게 하되 공간은 유지
        loadMoreBtn.style.visibility = "hidden";

        try {
            const res = await fetch(`http://localhost:8080/api/music/spotify/artist/albums?artistId=${artistId}&page=${page}&size=${size}`);
            const data = await res.json();

            if (data.success && data.success.responseData) {
                const { items, hasNext } = data.success.responseData;
                renderAlbums(items);
                console.log(items);

                // hasNext 값에 따라 더보기 버튼 표시/숨김
                if (hasNext) {
                    loadMoreBtn.style.display = "block";
                    loadMoreBtn.style.visibility = "visible";
                } else {
                    loadMoreBtn.style.display = "none";
                }
            }
        } catch (err) {
            console.error("앨범 불러오기 실패:", err);
        } finally {
            // 로딩 스피너 숨기기
            loadingContainer.classList.add('hidden');
        }
    }

    function renderAlbums(albums) {
        const list = document.getElementById("album-list");

        albums.forEach(album => {
            const card = document.createElement("div");
            card.className = "album-card";
            // 아티스트들을 개별 a태그 생성
            const artistsHtml = (album.artists || []).map((artist, index) => {
                const comma = index > 0 ? ', ' : '';
                return `${comma}<a href="/music/artist/${artist.artistId || ''}" class="artist-item">${artist.artistName}</a>`;
            }).join('');

            card.innerHTML = `
    <div class="album-image">   
      <img src="${album.albumImageUrl}" alt="${album.albumName}">
    </div>
      <div class="album-details">
          <div class="album-info">
              <div class="album-name">${album.albumName}</div>
              <div class="album-date">${album.releaseDate}</div>
              <div class="album-artists">${artistsHtml}</div>
          </div>
        
          <a class="spotify-link" href="${album.albumSpotifyUrl}" target="_blank">Spotify</a>
      </div>
    `;
            list.appendChild(card);
        });
    }

// 더보기 버튼 클릭 시 다음 페이지 로드
    document.getElementById("load-more").addEventListener("click", () => {
        currentPage++;
        fetchAlbums(currentPage);
    });

// 첫 페이지 로드
    fetchAlbums(currentPage);



});
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

// 배경 그라데이션 적용 함수
function applyGradientBackground(colors) {
    const artistProfile = document.querySelector('.artist-profile');
    const gradient = `linear-gradient(180deg, ${colors.lightColor} 0%, ${colors.lightColor} 80%, ${colors.darkColor} 100%)`;
    artistProfile.style.background = gradient;
}