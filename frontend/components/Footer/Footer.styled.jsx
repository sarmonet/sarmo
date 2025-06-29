import styled from '@emotion/styled'
import { colors, mq } from '../../utils'

export const FooterWrapper = styled.section`
	position: relative;
	margin-top: 174px;
	background: ${colors.blueHeroBack};
`
export const FooterContent = styled.div`
	display: flex;
	flex-wrap: wrap;
	padding: 30px 0;
	gap: 100px;
	color: ${colors.mainWhiteTextColor};

	@media (max-width: 768px) {
		max-width: 607px;
		margin: 0 auto;
		flex-direction: column;
		h2 {
			margin: 0 auto;
			width: 220px;
		}
	}

	@media (max-width: 1024px) {
		max-width: none;
		flex-wrap: initial;
	}
`
export const FooterBody = styled.div`
	display: flex;
	flex-wrap: wrap;

	padding: 30px 0;
	gap: 100px;
	color: ${colors.mainWhiteTextColor};

	@media (max-width: 768px) {
		max-width: 607px;
		margin: 0 auto;
		flex-direction: column;
	}

	@media (max-width: 1024px) {
		max-width: none;
		flex-wrap: initial;
	}
`

export const FooterAbout = styled.div`
	display: flex;
	flex-direction: column;

	span {
		margin-bottom: 10px;
		font-size: 22px;
		line-height: 34px;
		font-weight: 400;
	}
	a {
		color: inherit;
	}

	ul {
		display: flex;
		flex-direction: column;
	}
	li {
		font-size: 16px;
		line-height: 26px;
		font-weight: 500;
	}
	@media (max-width: 768px) {
		span {
			font-size: 24px;
			line-height: 32px;
		}
		li {
			font-size: 21px;
			line-height: 28px;
		}
	}

	@media (max-width: 1024px) {
	}
`

export const SubFooter = styled.div`
	background-color: ${colors.purpleBgColor};
	color: ${colors.darkBlueBgColor};
`
export const SubContent = styled.div`
	font-weight: 400;
	display: flex;
	align-items: center;
	justify-content: space-between;

	padding: 7px 0;
	${mq.smallMobile} {
		font-size: 12px;
	}
	${mq.tablet} {
		font-size: 14px;
	}
	${mq.desktop} {
		font-size: 14px;
	}

	a {
		letter-spacing: 0.05em;
		color: ${colors.mainWhiteTextColor};
	}
`
