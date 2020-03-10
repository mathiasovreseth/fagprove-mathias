import styled from 'styled-components';


export const H1 = styled.h1`
  color: #333;

  font-family: 'Lato', sans-serif;
  font-weight: 600;
  line-height: 2.75em; // 44px;
  font-size: 2em; // 32px;
  margin: 0;
`;

export const H2 = styled.h2`
  color: #333;
  font-family: 'Lato', sans-serif;
  font-weight: normal;
  line-height: 2em; // 32px;
  font-size: 1.375em; // 22px;
  margin: 0;
`;

export const H3 = styled.h3`
  color: #333;
  font-family: 'Lato', sans-serif;
  font-weight: normal;
  line-height: 1.75em; // 28px;
  font-size: 1.25em; // 20px;
  margin: 0;
`;

export const H4 = styled.h4`
  color: #333;
  font-family: 'Lato', sans-serif;
  font-weight: normal;
  line-height: 1.5em; // 28px;
  font-size: 1.15em; // 20px;
  margin: 0;
`;

export const TextNormal = styled.div`
  color: #333;
  font-family: 'Lato', sans-serif;
  font-size: 1em; // 16px
  line-height: 1.5em; // 24px
`;

export const TextSmall = styled.div`
  color: #333;
  font-family: 'Lato', sans-serif;
  font-size: 0.875em; // 14px
  line-height: 1.375em; // 22px
`;
export const FormLabel = styled.div`
  color: #333;
  font-family: 'Lato', sans-serif;
  font-size: 0.875em; // 14px
  line-height: 1.375em; // 22px
  font-weight: bold;
  margin-bottom: .25em;
`;

export const TextVerySmall = styled.div`
  color: #333;
  font-family: 'Lato', sans-serif;
  font-size: 0.75em; // 12px
  line-height: 1.188em; // 19px?
`;

export const TextLarge = styled.div`
  color: #333;
  font-family: 'Lato', sans-serif;
  font-size: 1.125em; // 18px
  line-height: 2em; // 32px
`;

export const FlexDiv = styled.div`
display: flex;
align-items: center;
`;

export const MainLayoutContainer = styled.div`
  background-color: #fff;
  width: 50%;
  border-radius: 4px;
  box-sizing: border-box;
  padding: 1.5em;
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  margin: 2em;

  height: 100vh;
  
`;

export const Input = styled.input`
  border-radius: 4px;
  width: 260px;
  padding: .25em .5em;
`;
