import { colors } from '@/utils'
import styled from '@emotion/styled'
import Image from "next/legacy/image"
export const BlogHeroWrapper = styled.div`
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 96px 0;
	justify-content: center;
	row-gap: 20px;
	text-align: center;

	span {
		font-size: 14px;
		padding: 5px 10px;
		border-radius: 180px;
		border: 1px solid ${colors.btnMainColor};
	}
	h1 {
		font-size: 40px;
		font-weight: 600;
		line-height: 1.2;
	}
	p {
		font-size: 20px;
		line-height: 1.5;
		color: ${colors.btnMainColor};
	}
`
export const BlogContentList = styled.ul`
	display: grid;
	grid-template-columns: repeat(4, 1fr);
	gap: 20px;
	@media (max-width: 1372px) {
		grid-template-columns: repeat(3, 1fr);
	}

	@media (max-width: 992px) {
		grid-template-columns: repeat(2, 1fr);
	}

	@media (max-width: 600px) {
		grid-template-columns: repeat(1, 1fr);
	}
`
export const BlogItem = styled.div`
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	row-gap: 18px;
	padding: 24px 24px 32px 24px;
	border: 1px solid ${colors.borderColor};
	border-radius: 8px;
	height: 450px;
	cursor: pointer;
`

export const BlogImage = styled(Image)`
	width: 100%;
	height: 100%;
	max-height: 220px;
	object-fit: cover;
`
export const BlogMain = styled.div`
	display: flex;
	flex-direction: column;
	justify-content: flex-start;
	row-gap: 10px;
	margin-bottom: auto;

	h3 {
		text-align: start;
		font-size: 18px;
		font-weight: 500;
	}
	p {
		font-size: 14px;
		color: ${colors.greyTextColor};
		line-height: 1.5;
	}
`
export const BlogFooter = styled.div`
	display: flex;
	align-items: center;
	column-gap: 10px;
	div {
		display: flex;
		flex-direction: column;
		row-gap: 2px;
		h4 {
			font-size: 13px;
			font-weight: 600;
		}
		span {
			font-size: 12px;
			color: ${colors.greyTextColor};
		}
	}
`
export const BlogPageWrapper = styled.article`
	display: flex;
	flex-direction: column;
	row-gap: 20px;

	h1 {
		font-size: 35px;
		letter-spacing: 0.03em;
		font-weight: 600;
		line-height: 120%;
	}
	ol,
	ul {
		list-style: initial;
	}
	a {
		color: ${colors.btnMainColor};
		text-decoration: underline;
	}
	span {
		font-size: 14px;
		font-weight: 400;
		color: ${colors.greyTextColor};
	}

	p {
		font-size: 17px;
		line-height: 175%;
		padding-left: 20px;
		margin: 20px 0;
		width: 80%;
	}
	img {
		object-fit: cover;
		margin-left: 150px;
		border-radius: 8px;
	}
`
export const BlogPageTop = styled.div`
	display: flex;
	flex-direction: column;
	row-gap: 15px;
	img {
		height: 466px;
	}
`
export const BlogPageMain = styled.div`
	img {
		position: relative;
		max-height: 500px;
		min-width: 1000px;
		object-fit: cover;
	}
`
export const BlogPageImageBlock = styled.div`
	p {
		font-size: 16px;
		text-align: center;
		color: ${colors.greyTextColor};
	}
`
