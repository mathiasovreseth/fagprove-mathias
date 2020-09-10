import React, { useEffect } from 'react';
import styled from 'styled-components';
import {
  BrowserRouter as Router,
  Switch,
  Route, Redirect,
} from "react-router-dom";
import { UserCreatePage } from './UserCreatePage';
import { AdminRoute, AuthRoute, NoAccessComp } from './Auth';
import { CandidatesPage } from './CandidatesPage';
import Header from './Header';
import { Home } from './Home';
import { Login } from './LoginPage';
import { MainLayoutContainer } from './BasicStyles';
import { Committees } from './Committees';
import { CalendarPage } from './CalendarPage';
import { CreateExaminationPage } from './CreateExaminationPage';
import { EditExaminationPage } from './EditExemenationPage';

const MainContainer = styled.div`
  width: 100%;
  height: 100vh;
  background-color: #f3f3f3;
  
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
  

`;


export async function superRequest(url, data) {
  const token = localStorage.getItem('token');
  const req = new Request(url, {
    method: 'POST',
    body: JSON.stringify(
      data
    ),
    headers: new Headers({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
      Pragma: 'no-cache',
    }),
  });

  return fetch(req).then(response => {
    if (response.status === 409 || response.status === 403 || response.status === 400) {
      return response.text()
    }
    if (response.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('role');
      return <Redirect to={'/login'}/>
    }

    return response.json();
  }).then(data => {
    return data;
  })
}

export const IsAdminContext = React.createContext(false);

function App() {
  const [isLoggedIn, setIsLoggedIn] = React.useState(undefined);
  const [me, setMe] = React.useState(undefined);
  const [role, setRole] = React.useState('ROLE_USER');

  async function getMe() {
    const fetch = await superRequest('http://localhost:8080/person/me', {});
    const res = await fetch;

    setMe(res);
  }

  useEffect(() => {
    if (localStorage.getItem('token')) {
      setIsLoggedIn(true);
      switch (localStorage.getItem('role')) {
        case 'ROLE_ADMIN':
          setRole('ROLE_ADMIN');
          break;
        case 'ROLE_USER':
          setRole('ROLE_USER');
          break;
        case 'ROLE_MANAGER':
          setRole('ROLE_MANAGER');
          break;
        default:
          setRole('ROLE_USER');
      }
      if (localStorage.getItem('role') === 'ROLE_ADMIN') {
        setRole('ROLE_ADMIN');
      }
    } else {
      setIsLoggedIn(false);
    }
    getMe()
  }, []);

  if (isLoggedIn === undefined) {
    return <div/>;
  }

  if (!isLoggedIn) {
    return (
      <MainContainer>
        <MainLayoutContainer>
          <Redirect to={'/login'}/>
          <Login/>
        </MainLayoutContainer>
      </MainContainer>
    )
  }
  return (
    <Router>
      <MainContainer>
        <IsAdminContext.Provider value={{
          role: role,
          me: me
        }}>
          <Header onClickHeaderItem={() => {
            localStorage.removeItem('token');
            localStorage.removeItem('role');
            window.location.reload();
          }}/>
          <Switch>
            <MainLayoutContainer>
              {role === 'ROLE_ADMIN' &&
              <AuthRoute exact={true} path="/committees" component={Committees}/>
              }
              <AuthRoute exact={true} path="/candidates" component={CandidatesPage}/>

              <AuthRoute exact={true} path="/create/user" component={UserCreatePage}/>
              <Route exact={true} path={['/', '/home']} component={Home}/>
              <AuthRoute exact={true} path={['/calendar']} component={CalendarPage}/>
              <AuthRoute exact={true} path={['/calendar/create/examination']} component={CreateExaminationPage}/>
              <Route exact={true} path={'/noAccess'} component={NoAccessComp}/>
            </MainLayoutContainer>
          </Switch>
        </IsAdminContext.Provider>
      </MainContainer>
    </Router>
  );
}

export default App;


