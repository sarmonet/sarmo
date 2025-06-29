import { colors, mq } from '@/utils'
import styled from '@emotion/styled'

export const HomeCategoriesWrapper = styled.div`
	margin: 80px 0;
	background-color: ${colors.mainWhiteTextColor};
	${mq.tablet} {
	}

	${mq.desktop} {
	}

	${mq.largeDesktop} {
	}
`

export const HomeCategoriesFilter = styled.div`
	border: 1px solid #e5e1e1;
	border-radius: 20px;
	font-weight: 500;
	margin-top: 120px;

	h3 {
		font-size: 28px;
		line-height: 38px;
		margin-bottom: 16px;
		padding: 0 30px;
	}

	ul {
		display: grid;
		grid-template-columns: repeat(auto-fill, minmax(279px, 1fr));
		flex-wrap: wrap;
		row-gap: 10px;
		column-gap: 50px;
		padding: 0 30px;
	}

	li {
		width: 279px;
		line-height: 26px;
		font-size: 18px;
		cursor: pointer;
	}

	${mq.tablet} {
	}

	${mq.desktop} {
		padding: 30px;
	}

	${mq.largeDesktop} {
	}
`

export const HomeCategoriesContent = styled.div`
	display: flex;
	font-weight: 500;
	padding: 22px 0;
	align-items: center;
	gap: 40px;
	font-size: 18px;
	line-height: 26px;
	flex-wrap: wrap;

	div {
		text-align: center;
		cursor: pointer;
		transition: 0.3s ease 0s;
		height: 140px;
		max-width: 140px;

		img {
			object-fit: fill;
			border-radius: 50%;
		}

		p {
			margin-top: 5px;
			color: ${colors.subtitleTextColor};
		}
	}

	@media (max-width: 1024px) {
		justify-content: center;
		gap: 40px;

		div {
			transform: scale(1.2);
			transition: 0.3s ease 0s;
		}
	}
	@media (max-width: 768px) {
		row-gap: 70px;
	}
`

export const MobileFilterOverlay = styled.div`
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background-color: rgba(0, 0, 0, 0.5);
	z-index: 1000;
`

export const MobileFilterPopup = styled.div`
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background-color: ${colors.mainWhiteTextColor};
	z-index: 1001;
	padding: 30px;
	display: flex;
	flex-direction: column;
	align-items: flex-start;
	overflow-y: auto;

	h3 {
		font-size: 32px;
		line-height: 42px;
		margin-bottom: 20px;
	}

	ul {
		display: flex;
		flex-direction: column;
		gap: 15px;
		width: 100%;
	}

	li {
		font-size: 20px;
		line-height: 30px;
		cursor: pointer;
		padding-bottom: 10px;
		border-bottom: 1px solid #e5e1e1;
		width: 100%;
	}

	li:last-child {
		border-bottom: none;
	}

	button {
		position: absolute;
		top: 0;
		right: 20px;
		margin-top: 30px;
		padding: 10px 20px;
		font-size: 18px;
		font-weight: 500;
		background-color: ${colors.btnMainColor};
		color: ${colors.mainWhiteTextColor};
		border: none;
		border-radius: 10px;
		cursor: pointer;
	}
`
