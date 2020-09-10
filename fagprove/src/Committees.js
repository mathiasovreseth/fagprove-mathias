import React, { useContext, useEffect } from 'react';
import { IsAdminContext, superRequest } from './App';
import { FlexDiv } from './BasicStyles';
import { Link } from 'react-router-dom';
import { Button } from './Button';
import styled from 'styled-components';


const OuterDiv = styled.div`
table, th, td {
  border: 1px solid black;
}
`;


export function Committees() {
  const adminConsumer = useContext(IsAdminContext);
  const [examinators, setExaminators] = React.useState();
  const [comitees, setComitees] = React.useState();

  async function getComitees() {
    const fetch = await superRequest(' http://localhost:8080/committee/list', {});
    const res = await fetch;

    setComitees(res);
  }
  async function getExaminators() {
    const fetch = await superRequest('http://localhost:8080/person/listExaminators', {});
    const res = await fetch;

    setExaminators(res);
  }


  useEffect(() => {
    getComitees();
    getExaminators();
  }, []);

  console.log(examinators);
  return (
      <OuterDiv style={{width: '100%'}}>
        Medlemmer
        <FlexDiv>
        <table>
          <tbody>
          <tr>
            <th>Navn</th>
            <th>Mobil</th>
            <th>epost</th>
            <th>rolle</th>
          </tr>

          {examinators && examinators?.map(c => {
            return <tr style={{marginLeft: '2em'}}>
              <td>{c.name}</td>
              <td>{c?.phoneNumber ?? ''}</td>
              <td>{c?.email ?? ''}</td>
              <td>{c?.jobRole ?? 'Ikkje satt'}</td>
            </tr>
          })}

          </tbody>
        </table>
          <div style={{marginLeft: '4em'}}>
        <table>
          <tbody>
          <tr>
            <th>Nemnder</th>
          </tr>

          Nemnder
          {comitees && comitees.map(c => {
            return <tr style={{marginLeft: '2em'}}>
              <td>{c.name}</td>
            </tr>
          })}
          </tbody>
        </table>
          </div>

        </FlexDiv>
        </OuterDiv>

        )
        }