import { colors, mq } from '@/utils'
import styled from '@emotion/styled'

export const AnchorMenu = styled.ul`
	display: flex;
	align-items: center;
	flex-wrap: wrap;
	justify-content: space-around;
	padding-top: 10px;
	border-bottom: 1px solid ${colors.borderColor};
	@media (max-width: 768px) {
		display: grid;
		place-items: center;
				text-align: center;

		grid-template-columns: repeat(2, 1fr);
	}
	@media (max-width: 425px) {
		display: none;
	}
	
	li{
		position: relative;
		font-size: 18px;
		font-weight: 500;
		line-height: 26px;
		padding: 10px 16px 20px 16px;
		cursor: pointer;
		&:after{
			content: '';
			position: absolute;
			bottom: 0;
			left: 50%;
			width:00%;
			height: 1px;
			background-color: ${colors.btnMainColor};
			transition: all 0.3s ease 0s; 

		}
		&:hover{
			&:after{
				width: 100%;
				left: 0;
			}
	}
			
			
`
export const ListingSection = styled.section`
	input {
		width: 100%;
		padding: 20px 15px;
		border: 1px solid ${colors.secondBorderColor};
		border-radius: 180px;
		margin-top: 30px;
		outline: none;
		font-size: 18px;
		font-weight: 500;
		line-height: 26px;
	}
	@media (max-width: 1024px) {
		input {
			width: 100%;
			padding: 15px 10px;
			border: 1px solid ${colors.secondBorderColor};
			border-radius: 180px;
			margin-top: 30px;
			outline: none;
			font-size: 18px;
			font-weight: 500;
			line-height: 26px;
		}
	}
`

export const ListingWrapper = styled.div`
	display: flex;
	gap: 30px;
	@media (max-width: 1024px) {
		flex-direction: column;
	}
`
export const ListingLeft = styled.div`
	display: flex;
	flex-direction: column;
	row-gap: 60px;
	padding-right: 20px;
	margin-top: 20px;
	border-right: 1px solid ${colors.borderColor};
	${mq.desktop} {
		width: 820px;
	}
	${mq.largeDesktop} {
		width: 920px;
	}
`
export const ListingNavigation = styled.ul`
	display: flex;
	align-items: center;
	flex-wrap: wrap;
	column-gap: 25px;
	margin-top: 40px;
	li {
		position: relative;
		&:after {
			content: '/';
			position: absolute;
			right: -15px;
			top: 0;
		}
	}
`
export const ListingName = styled.h1`
	font-size: 45px;
	font-weight: 500;
	line-height: 56px;
	@media (max-width: 1024px) {
		font-size: 35px;
		line-height: 46px;
	}
`
export const description = styled.p`
	font-size: 45px;
	font-weight: 500;
	line-height: 56px;
`
export const CountView = styled.span`
	display: flex;
	align-items: center;
	column-gap: 10px;
	font-size: 18px;
	font-weight: 500;
	color: ${colors.SecondGreyTextColor};
`
export const ListingSubTitle = styled.div`
	display: flex;
	align-items: center;
	justify-content: space-between;
	h4 {
		font-size: 22px;
		font-weight: 400;
		line-height: 34px;
		color: ${colors.subtitleTextColor};
	}
	span {
		font-size: 21px;
		font-weight: 400;
		line-height: 34px;
		padding: 5px 13px;
		text-transform: uppercase;
		border-radius: 180px;
		border: 1px solid ${colors.borderColor};
		color: ${colors.btnSecondColor};
	}
`

export const ListingMain = styled.div`
	display: flex;
	flex-direction: column;
	row-gap: 30px;
	cursor: pointer;
`
export const ListingFilters = styled.div`
	display: flex;
	flex-wrap: wrap;
	gap: 20px;
	padding: 10px 20px;
	font-size: 18px;
	font-weight: 500;
	line-height: 26px;
	border-radius: 180px;
	button {
		display: flex;
		column-gap: 8px;
		padding: 10px 20px;
		border: 1px solid ${colors.borderColor};
		font-size: 18px;
		font-weight: 500;
		line-height: 26px;
		border-radius: 180px;
		color: ${colors.greyTextColor};
	}
	@media (max-width: 768px) {
		gap: 10px;
		button {
			border: none;
			text-align: start;
			padding: 10px;
			font-size: 16px;
		}
	}
`

export const ListingAboutTitle = styled.h2`
	font-size: 40px;
	font-weight: 400;
	line-height: 100%;
`
export const ListingAboutSubTitle = styled.p`
	font-size: 18px;
	font-weight: 400;
	line-height: 26px;
	colors: ${colors.subtitleTextColor};
`

export const ListingRight = styled.div`
	display: flex;
	flex-direction: column;
	row-gap: 30px;
	padding-right: 20px;
	flex-grow: 1;
	border-right: 1px solid ${colors.borderColor};
`

export const ListingRightTop = styled.div`
	display: flex;
	justify-content: space-between;
`

export const ListingRightBottom = styled.div`
	display: flex;
	flex-direction: column;
	row-gap: 30px;
	@media (max-width: 1024px) {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		gap: 40px;
	}
	@media (max-width: 768px) {
		display: flex;
		flex-direction: column;
	}
`

export const ListingRightTopActivity = styled.div`
	display: flex;
	align-items: center;
	justify-content: space-between;
	span{
		padding: 10px;
		border: 1px solid ${colors.borderColor};
		border-radius: 50%;
		cursor: pointer;
		transition: all 0.3s ease;
	}	
		span:nth-of-type(2):hover{
		transform: scale(1.1);
	}
		span:nth-of-type(1):hover{
		transform: rotate(-15deg);
		svg{
		color: ${colors.btnMainColor}
		;
	}
`
export const ListingRightTitle = styled.div`
	width: 256px;

	h2 {
		font-size: 40px;
		font-weight: 400;
		line-height: 100%;
		padding-bottom: 30px;
	}
	span {
		font-size: 45px;
		font-weight: 500;
		line-height: 56%;
		color: ${colors.btnMainColor};
	}

	@media (max-width: 1024px) {
		display: flex;
		flex-wrap: wrap;
		align-items: center;
		column-gap: 20px;
		width: 100%;

		span {
			transform: translateY(-12px);
		}
	}
	@media (max-width: 480px) {
		h2 {
			font-size: 35px;
			padding-bottom: 30px;
		}
		span {
			font-size: 35px;
			font-weight: 400;
			line-height: 56%;
		}
	}
`
export const ListingRightRating = styled.div`
	display: flex;
	flex-direction: column;
	row-gap: 10px;
	font-size: 28px;
	font-weight: 500;
	line-height: 38px;
	p {
		display: flex;
		align-items: center;
		column-gap: 5px;
		font-size: 22px;
		font-weight: 400;
		line-height: 34px;
	}
	span {
		margin-left: 8px;
		color: ${colors.SecondGreyTextColor};
	}
`
export const ListingBottomRating = styled.div`
	display: flex;
	flex-direction: column;
	row-gap: 10px;
	font-size: 28px;
	font-weight: 500;
	line-height: 38px;
	p {
		display: flex;
		align-items: center;
		column-gap: 5px;
		font-size: 22px;
		font-weight: 400;
		line-height: 34px;
	}
	span {
		margin-left: 8px;
		color: ${colors.SecondGreyTextColor};
	}
	div {
		display: flex;
		align-items: center;
		column-gap: 4px;
	}
`
export const ListingRightInfo = styled.ul`
	display: flex;
	flex-direction: column;
	row-gap: 10px;
	padding-right: 50px;
	li {
		display: flex;
		justify-content: space-between;
		align-items: center;
		font-size: 18px;
		font-weight: 400;
		line-height: 26px;
		color: ${colors.categoryLink};
	}

	span {
		text-align: right;
		color: ${colors.subtitleTextColor};
	}
`

export const ListingSpecialist = styled.div`
	padding: 30px;
	border-radius: 20px;
	border: 1px solid ${colors.borderColor};
	button {
		width: 100%;
	}
	@media (max-width: 1024px) {
		display: flex;
		flex-direction: column;
		justify-content: space-between;
	}
`
export const ListingDetails = styled.div`
	display: flex;
	flex-direction: column;
	row-gap: 75px;
	padding: 30px 0 100px 0;
`
export const ListingDetail = styled.div`
	h3 {
		font-size: 40px;
		font-weight: 400;
		line-height: 100%;
		margin-bottom: 30px;
	}
	p {
		font-size: 18px;
		font-weight: 400;
		line-height: 26px;
		color: ${colors.darkBlueBgColor};
	}
`
export const ShareStyle = styled.button`
	display: flex;
	gap: 10px;
	align-items: center;
	justify-content: center;
	width: fit-content;
`
export const ListingSpecialistHat = styled.div`
	display: flex;
	flex-direction: column;
	row-gap: 30px;
	img {
		border-radius: 50%;
		height: 90px;
		width: 90px;
		background-color: ${colors.borderColor};
	}
	h3 {
		font-size: 24px;
		font-weight: 500;
		line-height: 38px;
	}
	p {
		font-size: 22px;
		font-weight: 400;
		line-height: 34px;
		color: ${colors.greyTextColor};
	}
`
