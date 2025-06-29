"use client";
import { AdaptiveContainer } from "./Container.styled"

const Container = ({ children }) => {
  return (
    <>
      <AdaptiveContainer >{children}</AdaptiveContainer>
    </>
  );
};

export { Container }

