
import styled from "@emotion/styled";

export const Preloader = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 999999;
  background: transparent;
`;

export const LoaderContainer = styled.div`
  display: block;
  position: relative;
  left: 50%;
  top: 50%;
  width: 80px;
  height: 80px;
  margin: -75px 0 0 -75px;
  border-radius: 50%;
  border: 3px solid transparent;
  border-top-color: #306791;
  animation: spin 2s linear infinite;

  &:before {
    content: "";
    position: absolute;
    top: 5px;
    left: 5px;
    right: 5px;
    bottom: 5px;
    border-radius: 50%;
    border: 3px solid transparent;
    border-top-color: #7c9154;
    animation: spin 3s linear infinite;
  }

  &:after {
    content: "";
    position: absolute;
    top: 15px;
    left: 15px;
    right: 15px;
    bottom: 15px;
    border-radius: 50%;
    border: 3px solid transparent;
    border-top-color: #b27132;
    animation: spin 1.5s linear infinite;
  }

  @keyframes spin {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  }
`;
