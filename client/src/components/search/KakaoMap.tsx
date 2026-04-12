
import { Map, MapMarker, useKakaoLoader } from "react-kakao-maps-sdk"
import type { Store } from "../../type/search"
import { useEffect, useState } from "react"

type KakaoMapProps = {
    selectedStore: Store | null
}

export const KakaoMap = ({ selectedStore }: KakaoMapProps) => {
    //kakao sdk 로드 (appkey로 인증, services 라이브러리 = Geocoder 사용하기 위함)
    const [loading, error] = useKakaoLoader({
        appkey: "9e174327e283f4f12d6e459138fee8f5",
        libraries: ["services"]
    })

    // 지도 중심 좌표 상태 (초기값 = 전남 중심)
    const [center, setCenter] = useState<{ lat: number; lng: number }>({
        lat: 34.8,
        lng: 126.9
    })

    if (loading) return <div>지도 로딩중</div>
    if (error) return <div>지도 로드 실패</div>
    return (
        <Map
            //center 바뀔때마다 지도 중심 이동
            center={center}
            style={{ width: "100%", height: "900px" }}
            level={4}
        >
            {selectedStore && (
                <MapMarkerWithAddress
                    store={selectedStore}
                    onPosition={setCenter} />
            )}
        </Map>
    )
}

// 주소 → 좌표 변환 후 마커 표시
const MapMarkerWithAddress = ({ store, onPosition }: {
    store: Store
    onPosition: (pos: { lat: number; lng: number }) => void
}) => {
    //초기값 null
    const [position, setPosition] = useState<{ lat: number; lng: number } | null>(null)

    useEffect(() => {
        //geocoder 생성
        const geocoder = new kakao.maps.services.Geocoder();
        //주소>위경도변환요청 - 비동기콜백
        geocoder.addressSearch(store.address, (result, status) => {
            //변환성공시
            if (status === kakao.maps.services.Status.OK) {
                const pos = {
                    lat: parseFloat(result[0].y),  // 위도
                    lng: parseFloat(result[0].x)   // 경도
                }
                setPosition(pos);
                onPosition(pos);
            }
        })
    }, [store]);

    if (!position) return null

    return (
        <MapMarker position={position}>
            <div style={{ padding: "5px" }}>{store.storeName}</div>
        </MapMarker>
    )
}