import React from "react";
import styled from "styled-components";
import { BACKEND_IMAGE_URL } from "../../api";
import { flexCenter } from "../../styles/Basic";

interface IPropTypes {
  picPath: string | null;
  name: string;
  price: number;
  onClick: () => void;
}

const Container = styled.div`
  width: 80%;
  height: 120px;
  ${flexCenter}
  padding: 1rem 0;

  &:hover {
    cursor: pointer;
  }
`;

interface IImageContainer {
  url?: any;
}
const ImageContainer = styled.div<IImageContainer>`
  width: 100px;
  height: 100%;

  background-image: url(${({ url }) => url});
  background-size: cover;
  background-position: center;
`;

const ContentContainer = styled.div`
  flex: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 0 1rem;
`;

const BnbName = styled.div`
  font-weight: bold;
  font-size: 1.1rem;
  padding-left: 1rem;
`;

const BnbPrice = styled.div`
  align-self: flex-end;
  padding-bottom: 0.5rem;
  font-weight: bold;
  font-size: 1.1rem;
`;

const BnbList = ({ picPath, name, price, onClick }: IPropTypes) => {
  return (
    <Container onClick={onClick}>
      <ImageContainer
        url={picPath ? `${BACKEND_IMAGE_URL}${picPath}` : "/default.png"}
      ></ImageContainer>
      <ContentContainer>
        <BnbName>{name}</BnbName>
        <BnbPrice>{price.toLocaleString()}원</BnbPrice>
      </ContentContainer>
    </Container>
  );
};

export default BnbList;
