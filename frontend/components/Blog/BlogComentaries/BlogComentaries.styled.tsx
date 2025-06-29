import { colors } from '@/utils'
import styled from '@emotion/styled'

export const CommentariesWrapper = styled.div`
	display: flex;
	flex-direction: column;
	row-gap: 40px;
	padding-top: 40px;
	margin-bottom: 175px;

	img {
		border-radius: 50%;
		width: 48px;
		height: 48px;
	}
`

export const CommentariesHeader = styled.div`
	display: flex;
	align-items: center;
	column-gap: 8px;

	h2,
	span {
		font-size: 32px;
		font-weight: 500;
		line-height: 42px;
	}

	span {
		color: ${colors.SecondGreyTextColor};
		font-weight: 500;
		line-height: 42px;
	}
`

export const CommentariesInput = styled.div`
	position: relative;
	display: flex;
	column-gap: 14px;
	align-items: end;

	input,
	button {
		padding: 6px 12px;
		font-size: 18px;
		font-weight: 500;
		line-height: 26px;
		border-radius: 18px;
	}

	textarea {
		position: relative;
		width: 100%;
		height: 170px;
		padding: 20px 15px;
		border: 1px solid ${colors.borderColor};
		outline: none;
	}

	input {
		width: 100%;
		border: 1px solid ${colors.borderColor};
		color: ${colors.SecondGreyTextColor};
	}

	button {
		position: absolute;
		right: 5%;
		bottom: 10%;
		background-color: ${colors.btnSecondColor};
		color: ${colors.mainWhiteTextColor};
	}

	div {
		position: absolute;
		right: 0;
		top: -10%;
		font-size: 18px;
		padding: 3px 10px;
		cursor: pointer;
	}
`

export const ReplyInput = styled.div`
	position: relative;
	display: flex;
	column-gap: 14px;
	align-items: end;

	input,
	button {
		padding: 15px 12px;
		font-size: 18px;
		font-weight: 500;
		line-height: 26px;
		border-radius: 18px;
	}

	input {
		width: 100%;
		border: 1px solid ${colors.borderColor};
	}

	button {
		right: 5%;
		bottom: 15%;
		background-color: ${colors.btnSecondColor};
		color: ${colors.mainWhiteTextColor};
	}

	div {
		position: absolute;
		right: 0;
		top: -70%;
		font-size: 18px;
		padding: 3px 10px;
		cursor: pointer;
	}
`

export const CommentariesBlocks = styled.div`
	display: flex;
	flex-direction: column;
`

export const CommentariesBlock = styled.div`
	padding: 16px 0;
	border-bottom: 1px solid ${colors.borderColor};
	display: flex;
	flex-direction: column;
	gap: 16px;
	.img {
		width: 48px;
		height: 48px;
		border-radius: 50%;
		margin: 0%;
	}
`

export const CommentariesBody = styled.div`
	display: flex;
	flex-direction: column;
	flex: 1;

	h3 {
		font-size: 16px;
		font-weight: 600;
		margin: 0;
	}

	span {
		font-size: 13px;
		color: ${colors.SecondGreyTextColor};
	}

	p {
		font-size: 15px;
		line-height: 1.6;
		color: ${colors.SecondGreyTextColor};
		margin: 12px 0;
	}
`

export const CommentariesReply = styled.div`
	color: ${colors.btnMainColor};
	display: flex;
	align-items: center;
	column-gap: 8px;
	font-size: 14px;
	font-weight: 700;
	padding-left: 20px;
`

export const CommentariesDelete = styled.button`
	font-size: 14px;
	font-weight: 700;
	color: red;
`

export const CommentariesReplyButton = styled.button`
	display: flex;
	align-items: center;
	column-gap: 28px;
	margin-bottom: 30px;
`
