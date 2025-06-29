import { colors } from '@/utils'
import styled from '@emotion/styled'
interface props {
	color?: string
}

export const CatalogMainWrapper = styled.div`
	padding-top: 40px;
	margin: 0 auto;
	width: 100%;
`
export const CatalogHeader = styled.div`
	display: flex;
	flex-direction: column;
	row-gap: 30px;

	input {
		width: 100%;
		border-radius: 180px;
		border: 1px solid ${colors.borderColor};
		padding: 15px 20px;
		outline: none;
		appearance: none;
	}
`
export const CatalogHeaderFilter = styled.div`
	display: flex;
	justify-content: space-between;
	flex-wrap: wrap-reverse;
	align-items: center;
	gap: 20px;
	border-color: ${colors.borderColor};
	div {
		display: flex;
		align-items: center;
		column-gap: 10px;
		a,
		p {
			transition: color 0.3s ease 0s;
			cursor: pointer;
			&: hover {
				color: ${colors.btnMainColor};
			}
		}
		p {
			span {
				margin-right: 10px;
			}
		}
	}
`
export const CatalogItems = styled.div`
	margin-top: 30px;
`
export const CatalogListings = styled.div`
	margin-top: 30px;
	display: grid;
	grid-template-columns: repeat(3, minmax(280px, 304px));
	row-gap: 20px;
	column-gap: 50px;

	width: fit-content;
	@media (max-width: 1440px) {
		row-gap: 15px;
		column-gap: 35px;
	}
	@media (max-width: 1320px) {
		grid-template-columns: repeat(2, minmax(280px, 304px));
	}
	@media (max-width: 1024px) {
		grid-template-columns: repeat(3, minmax(280px, 304px));
	}
	@media (max-width: 998px) {
		grid-template-columns: repeat(2, minmax(280px, 304px));
	}
	@media (max-width: 768px) {
		grid-template-columns: repeat(2, minmax(169px, 304px));
		column-gap: 25px;
	}
`
export const CatalogSimilar = styled.div`
	margin-top: 30px;
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(304px, 1fr));
	row-gap: 30px;
	column-gap: 20px;
`
export const CatalogItemsTitle = styled.h1`
	font-weight: 500;
	font-size: clamp(26px, 5vw, 45px);
	line-height: 56px;
`

export const ListingBlock = styled.div`
  position: relative;
  display: flex;
  flex-direction: column;
  row-gap: 22px;
  padding: 24px;
  border-radius: 20px;
  color: ${colors.mainTextColor};
  border: 1px solid #E5E1E1;
  cursor: pointer;

  .image-container {
    position: relative;
    width: 100%;
    overflow: hidden;
    border-radius: 10px; 
  }

  .image-container::before {
    content: "";
    display: block;
    padding-bottom: 100%; 
  }

  img {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  button{
    width: fit-content;
    transition: all 0.3s ease 0s;
    &:hover{
      background-color: ${colors.btnSecondColor};
    }
  }
  @media (max-width: 768px) {
    row-gap: 16px;
    padding: 16px;
    .image-container::before {
      padding-bottom: 100%; 
    }
    &:not(.notHide) button {
      display: none;
    }
    }
  @media (max-width: 480px) {
    padding: 13px;
    row-gap: 10px;
    .image-container::before {
      padding-bottom: 100%; 
    }
`
export const ListingMain = styled.div`
	display: flex;
	flex-direction: column;
	flex-grow: 1;
	justify-content: space-between;
	gap: clamp(13px, 1vw, 22px);
	@media (max-width: 768px) {
	}
`
export const ListingSpecial = styled.p<props>`
	border-radius: 180px;
	border: 1px solid ${colors.borderColor};
	color: ${({ color }) => (color ? color : colors.mainWhiteTextColor)};
	padding: 5px 10px;
	font-size: 18px;
	line-height: 26px;
	font-weight: 400;
`
export const ListingHorizontalBlock = styled.div`
	position: relative;
	display: flex;
	justify-content: space-between;
	align-items: center;
	column-gap: 20px;
	padding: 14px;
	border-radius: 20px;
	max-height: 100%;
	border: 1px solid #e5e1e1;
	margin-bottom: 20px;
	color: ${colors.mainTextColor};
	img {
		max-width: 210px;
		min-width: 170px;

		object-fit: cover;
		border-radius: 10px;
	}

	h3 {
		font-size: 32px;
		line-height: 42px;
		font-weight: 500;
	}
	.svgBlock {
		position: absolute;
		top: -3%;
		left: -3%;
		padding: 10px;
		background-color: ${colors.mainWhiteTextColor};
		border-radius: 50%;
		border: 1px solid ${colors.borderColor};
		cursor: pointer;
		transition: transform 0.3s ease;
		&:hover {
			transform: scale(1.1);
		}
	}
	@media (max-width: 1024px) {
		justify-content: flex-start;
	}
	button {
		width: fit-content;
		padding: 15px 25px;
		transition: all 0.3s ease 0s;
		&:hover {
			background-color: ${colors.btnSecondColor};
		}

		@media (max-width: 1024px) {
			display: none;
		}
	}

	@media (max-width: 1280px) {
		min-width: none;
	}
`

export const ListingRight = styled.div`
	display: flex;
	flex-direction: column;
	justify-content: space-around;
	align-items: flex-end;
	row-gap: 20px;
	h4 {
		font-size: 26px;
		line-height: 38px;
		font-weight: 500;
		color: ${colors.btnMainColor};
	}
	@media (max-width: 1024px) {
		h4 {
			font-size: 24px;
			line-height: 24px;
		}
	}
`

export const Favorite = styled.div`
	position: absolute;
	top: -2%;
	left: -2%;
	padding: 10px;
	border-radius: 50%;
	border: 1px solid ${colors.borderColor};
	background-color: ${colors.mainWhiteTextColor};
	transition: transform 0.3s ease;
	cursor: pointer;
	&:hover {
		transform: scale(1.1);
	}
	@media (max-width: 768px) {
		// top: -5%;
		// left: -5%;
		padding: 0px;
	}
`
export const Premium = styled.div`
	position: absolute;
	z-index: 10;
	left: 75%;
	bottom: 3%;
	padding: 4px 10px;
	font-size: 18px;
	background-color: ${colors.mainWhiteTextColor};
	border-radius: 180px;
	text-transform: uppercase;
	font-weight: 700;
	line-height: 120%;
	color: ${colors.btnSecondColor};
	@media (max-width: 768px) {
		font-size: 14px;
		padding: 4px 8px;
	}
	@media (max-width: 500px) {
		font-size: 10px;
		left: 70%;
	}
	@media (max-width: 480px) {
	}
`
export const EmptyState = styled.div`
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	width: 100%;
	padding: 40px;
	text-align: center;

	img {
		width: 200px;
		height: 200px;
		margin-bottom: 20px;
	}

	h3 {
		font-size: 24px;
		margin-bottom: 10px;
		color: ${colors.mainTextColor};
	}

	p {
		color: ${colors.greyTextColor};
		font-size: 16px;
	}
`

export const StyledList = styled.ul`
	position: absolute;
	width: 100%;
	z-index: 10;
	background-color: #fcfcfc;
	border: 1px solid ${colors.borderColor};
	list-style: none;
	padding: 0;
	margin: 0;
	border-radius: 5px;
	box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);

	li {
		padding: 10px 20px;
		font-weight: 500;
		font-size: 18px;
		line-height: 26px;
		color: ${colors.SecondGreyTextColor};
		cursor: pointer;
		transition: background-color 0.3s ease;

		&:hover {
			background-color: #f0f0f0;
		}
	}
`
export const CloseButton = styled.button`
	position: absolute;
	top: 10px;
	right: 10px;
	background: none;
	border: none;
	cursor: pointer;
	font-size: 20px;
	z-index: 10;
`

export const ListingTop = styled.div`
	display: block;
	border-radius: 20px;
	max-height: 84px;
	margin-bottom: 24px;

	h3 {
		font-weight: 500;
		font-size: clamp(25px, 3vw, 32px);
		line-height: 42px;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		max-width: clamp(100px, 10vw, 150px);
	}
	p {
		display: inline-block;
		position: relative;
		left: 0%;
		top: 10px;
		padding: 3px 10px 3px 10px;
		border-radius: 180px;
		font-weight: 400;
		font-size: 16px;
		line-height: 145%;
		border: 1px solid ${colors.borderColor};
		color: ${colors.btnMainColor};
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		max-width: clamp(100px, 10vw, 150px);
	}

	@media (max-width: 768px) {
		p {
			line-height: 125%;
			padding: 3px 7px 3px 7px;
			white-space: normal;
			overflow: visible;
			text-overflow: clip;
			max-width: none;
		}
	}
	@media (max-width: 511px) {
		p {
			white-space: nowrap;
			overflow: hidden;
			text-overflow: ellipsis;
			max-width: clamp(100px, 10vw, 150px);
		}
	}
`
export const ListingHorizontalAbout = styled.div`
	display: flex;
	align-items: flex-start;
	flex-direction: column;
	row-gap: 21px;
	// ul {
	//   display: flex;
	//   flex-direction: column;
	//   align-items: flex-start;
	// }

	li {
		display: flex;
		flex-wrap: wrap;
		align-items: center;
		justify-content: space-between;
		width: 100%;
		column-gap: 5px;
		color: ${colors.SecondGreyTextColor};
		span {
			color: ${colors.btnMainColor};
			flex-grow: 1;
		}
	}

	p,
	li,
	span {
		font-weight: 400;
		font-size: 14px;
		line-height: 26px;
	}
`

export const ListingAbout = styled.div`
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	position: relative;
	p {
		font-weight: 400;
		font-size: clamp(14px, 3vw, 18px);
		line-height: 26px;
		color: ${colors.greyTextColor};
	}

	span {
		font-weight: 500;
		font-size: clamp(15px, 3vw, 28px);
		line-height: 38px;
		color: ${colors.btnMainColor};
	}
	@media (max-width: 768px) {
		p {
			line-height: 18px;
		}
		span {
			line-height: 35px;
		}
	}
	@media (max-width: 425px) {
		span {
			line-height: 20px;
		}
	}

	color: ${colors.mainTextColor};
`

export const ListingInfo = styled.ul`
	display: flex;
	flex-direction: column;
	li {
		font-size: 18px;
		line-height: 26px;
		font-weight: 400;
		color: ${colors.subtitleTextColor};
	}
`

export const NoListingsMessage = styled.div`
	text-align: center;
	position: absolute;
	left: 50%;
	transform: translateX(-50%);
	padding: 40px 0;
	font-size: 2rem;
	color: ${colors.greyTextColor};
	min-height: 100%;
`
