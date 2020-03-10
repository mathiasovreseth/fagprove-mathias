import React, { useContext } from 'react';
import styled from 'styled-components';
import { IsAdminContext } from './App';
import { Link } from 'react-router-dom';
import {withRouter} from 'react-router-dom';
import { FlexDiv, H3 } from './BasicStyles';

const OuterDiv = styled.div`
  display: flex;
  width: 100%;  
  align-items: center;
  justify-content: space-between;
  padding: .5em 2em;
  box-sizing: border-box;
  background-color: #49494e;
`;

const HeaderItem = styled(H3)`
  margin: .5em;
  :hover {
    cursor: pointer;
  }
  .link {
    color:  ${props => props.isActive ? "#008CBA" : "#fff"};
  text-decoration: none; 
 }
  
`;

function HeaderComp(props) {
  const adminConsumer = useContext(IsAdminContext);
  return (
    <OuterDiv>
      <HeaderItem isActive={props.location.pathname.includes('/home')}>
        <Link className={'link'}  to={'/home'}>
          Hjem
        </Link>
      </HeaderItem>
      <FlexDiv>
        <HeaderItem isActive={props.location.pathname.includes('/users')}>
          <Link className={'link'}  to={'/candidates'}>
            Kandidatar
          </Link>
        </HeaderItem>
        {adminConsumer === 'ROLE_ADMIN' &&
        <HeaderItem isActive={props.location.pathname.includes('/users')}>
          <Link className={'link'}  to={'/committees'}>
            nemndmedlemmer
          </Link>
        </HeaderItem>
        }
        <HeaderItem isActive={props.location.pathname.includes('/calendar')}>
          <Link className={'link'}  to={'/calendar'}>
            kalender
          </Link>
        </HeaderItem>
        <HeaderItem isActive={true} className={'link'} style={{color: '#fff'}} onClick={() => {
          props.onClickHeaderItem()
        }}>
          Logg ut
        </HeaderItem>
      </FlexDiv>
    </OuterDiv>
  )
}

const Header = withRouter(HeaderComp);
export default Header;