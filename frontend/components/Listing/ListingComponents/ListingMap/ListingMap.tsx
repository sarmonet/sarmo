import { GoogleMap, Marker } from '@react-google-maps/api'
import { useEffect, useState } from 'react'

const containerStyle = {
	width: '100%',
	height: '288px',
	borderRadius: '12px',
}

interface ListingMapProps {
	city: string
	country: string
	fullAddress: string
}

export const ListingMap = ({ city, country, fullAddress }: ListingMapProps) => {
	const [position, setPosition] = useState<{ lat: number; lng: number } | null>(
		null
	)

	useEffect(() => {
		const geocoder = new window.google.maps.Geocoder()
		const address = `${fullAddress}, ${city}, ${country}`

		geocoder.geocode({ address }, (results, status) => {
			if (status === 'OK' && results && results[0]) {
				const location = results[0].geometry.location
				setPosition({ lat: location.lat(), lng: location.lng() })
			} else {
				console.error('Geocoding failed:', status)
			}
		})
	}, [city, country, fullAddress])

	return (
		<div className='listing-map'>
			{position && (
				<GoogleMap
					mapContainerStyle={containerStyle}
					center={position}
					zoom={12}
				>
					<Marker position={position} />
				</GoogleMap>
			)}
		</div>
	)
}
