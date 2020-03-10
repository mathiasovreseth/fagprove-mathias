import React from 'react';
import styled from 'styled-components';




const ButtonCont = styled.button`
  background-color: #008CBA;
  border: none;
  color: white;
  padding: .25em .5em;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
  min-width: 4em;
  border-radius: 4px;
  cursor: pointer;
  
`;










export function Button(props) {
  return (
    <ButtonCont {...props} />
  )
}