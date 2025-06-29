import Image from "next/legacy/image"


export const ProfilePayment = () => {
	 return(
		<>
			{/* <h3>Методы оплаты</h3> */}
			<div>
				<Image src={'/images/soon.jpg'} alt='coming soon' width={132} height={132} 
					style={{ width: '100%', height: '100%', objectFit: 'fill' }}
				/>
			</div>
		</>
	 )
}